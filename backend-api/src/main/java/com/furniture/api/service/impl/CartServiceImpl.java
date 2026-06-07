package com.furniture.api.service.impl;

import com.furniture.api.dto.request.AddToCartRequest;
import com.furniture.api.dto.request.UpdateCartItemRequest;
import com.furniture.api.dto.response.CartResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.model.Cart;
import com.furniture.api.model.CartItem;
import com.furniture.api.model.Product;
import com.furniture.api.model.ProductVariant;
import com.furniture.api.repository.CartItemRepository;
import com.furniture.api.repository.CartRepository;
import com.furniture.api.repository.ProductRepository;
import com.furniture.api.repository.ProductVariantRepository;
import com.furniture.api.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    @Transactional
    public CartResponse getCart(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(Integer userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int quantityToAdd = normaliseQuantity(request.getQuantity());
        ProductVariant variant = resolveVariant(product.getProductId(), request.getVariantId());
        Integer variantId = variant != null ? variant.getVariantId() : null;
        int availableStock = variant != null ? variant.getStock() : (product.getStock() != null ? product.getStock() : 0);
        if (availableStock < quantityToAdd) {
            throw new BadRequestException("Sản phẩm không đủ hàng");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId())
                        && (variantId == null ? item.getProductVariantId() == null
                        : variantId.equals(item.getProductVariantId())))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantityToAdd;
            if (availableStock < newQuantity) {
                throw new BadRequestException("Sản phẩm không đủ hàng");
            }
            item.setQuantity(newQuantity);
            item.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            cartItemRepository.save(item);
        } else {
            // Add new item - get price from variant or first variant of product
            if (variant == null) {
                throw new RuntimeException("Product has no price information");
            }
            BigDecimal price = variant.getPrice();

            CartItem newItem = CartItem.builder()
                    .cartId(cart.getCartId())
                    .productId(product.getProductId())
                    .productVariantId(variantId)
                    .quantity(quantityToAdd)
                    .price(price)
                    .totalPrice(price.multiply(BigDecimal.valueOf(quantityToAdd)))
                    .variantInfo(getVariantInfo(variant))
                    .build();

            cartItemRepository.save(newItem);
        }

        // Reload cart and recalculate total
        cart = cartRepository.findById(cart.getCartId()).orElse(cart);
        updateCartTotal(cart);

        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Integer userId, Integer cartItemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCartId().equals(cart.getCartId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        int quantity = normaliseQuantity(request.getQuantity());
        int availableStock = resolveStock(item);
        if (availableStock < quantity) {
            throw new BadRequestException("Sản phẩm không đủ hàng");
        }

        item.setQuantity(quantity);
        item.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(item);

        updateCartTotal(cart);
        return mapToCartResponse(cart);
    }

    @Override
    @Transactional
    public void removeCartItem(Integer userId, Integer cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCartId().equals(cart.getCartId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        cartItemRepository.delete(item);
        updateCartTotal(cart);
    }

    @Override
    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getCartId());
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Integer userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .totalPrice(BigDecimal.ZERO)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private int normaliseQuantity(Integer quantity) {
        if (quantity == null) {
            return 1;
        }
        if (quantity < 1) {
            throw new BadRequestException("Số lượng sản phẩm phải lớn hơn 0");
        }
        return quantity;
    }

    private ProductVariant resolveVariant(Integer productId, Integer requestedVariantId) {
        if (requestedVariantId != null) {
            ProductVariant variant = productVariantRepository.findById(requestedVariantId)
                    .orElseThrow(() -> new BadRequestException("Phân loại sản phẩm không tồn tại"));
            if (!productId.equals(variant.getProductId())) {
                throw new BadRequestException("Phân loại sản phẩm không thuộc sản phẩm đã chọn");
            }
            return variant;
        }

        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        if (variants.isEmpty()) {
            return null;
        }
        return variants.get(0);
    }

    private int resolveStock(CartItem item) {
        if (item.getProductVariantId() != null) {
            return productVariantRepository.findById(item.getProductVariantId())
                    .map(ProductVariant::getStock)
                    .orElseThrow(() -> new BadRequestException("Phân loại sản phẩm không tồn tại"));
        }
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));
        return product.getStock() != null ? product.getStock() : 0;
    }

    private void updateCartTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getCartId());
        BigDecimal total = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }

    private String getVariantInfo(ProductVariant variant) {
        StringBuilder info = new StringBuilder();
        if (variant.getColor() != null) info.append(variant.getColor());
        if (variant.getSize() != null) {
            if (info.length() > 0) info.append(" - ");
            info.append(variant.getSize());
        }
        if (variant.getMaterial() != null) {
            if (info.length() > 0) info.append(" - ");
            info.append(variant.getMaterial());
        }
        return info.toString();
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getCartId());

        List<CartResponse.CartItemResponse> itemResponses = items.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        int totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getCartId())
                .userId(cart.getUserId())
                .totalAmount(cart.getTotalPrice())
                .totalItems(totalItems)
                .items(itemResponses)
                .build();
    }

    private CartResponse.CartItemResponse mapToCartItemResponse(CartItem item) {
        Product product = productRepository.findById(item.getProductId()).orElse(null);
        ProductVariant variant = item.getProductVariantId() != null ?
                productVariantRepository.findById(item.getProductVariantId()).orElse(null) : null;

        String productImage = null;
        if (variant != null && variant.getImageUrl() != null) {
            productImage = variant.getImageUrl();
        } else if (product != null && product.getVariants() != null && !product.getVariants().isEmpty()) {
            productImage = product.getVariants().get(0).getImageUrl();
        }

        int stock = variant != null ? variant.getStock() : (product != null ? product.getStock() : 0);

        return CartResponse.CartItemResponse.builder()
                .cartItemId(item.getCartItemId())
                .productId(item.getProductId())
                .productName(product != null ? product.getProductName() : "Unknown")
                .productImage(productImage)
                .variantId(item.getProductVariantId())
                .variantName(item.getVariantInfo())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getTotalPrice())
                .stock(stock)
                .build();
    }
}
