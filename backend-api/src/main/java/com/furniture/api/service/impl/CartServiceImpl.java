package com.furniture.api.service.impl;

import com.furniture.api.dto.request.AddToCartRequest;
import com.furniture.api.dto.request.UpdateCartItemRequest;
import com.furniture.api.dto.response.CartResponse;
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

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId()).orElse(null);
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId())
                        && (request.getVariantId() == null ? item.getProductVariantId() == null
                        : request.getVariantId().equals(item.getProductVariantId())))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + (request.getQuantity() != null ? request.getQuantity() : 1));
            item.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            cartItemRepository.save(item);
        } else {
            // Add new item - get price from variant or first variant of product
            BigDecimal price;
            if (variant != null) {
                price = variant.getPrice();
            } else if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                price = product.getVariants().get(0).getPrice();
            } else {
                throw new RuntimeException("Product has no price information");
            }
            int quantity = request.getQuantity() != null ? request.getQuantity() : 1;

            CartItem newItem = CartItem.builder()
                    .cartId(cart.getCartId())
                    .productId(product.getProductId())
                    .productVariantId(request.getVariantId())
                    .shopId(product.getShopId())
                    .quantity(quantity)
                    .price(price)
                    .totalPrice(price.multiply(BigDecimal.valueOf(quantity)))
                    .variantInfo(variant != null ? getVariantInfo(variant) : null)
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

        item.setQuantity(request.getQuantity());
        item.setTotalPrice(item.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
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
                .shopId(item.getShopId())
                .shopName(product != null && product.getShop() != null ? product.getShop().getShopName() : null)
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getTotalPrice())
                .stock(stock)
                .build();
    }
}
