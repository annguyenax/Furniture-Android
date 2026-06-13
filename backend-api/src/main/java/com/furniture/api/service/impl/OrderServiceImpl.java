package com.furniture.api.service.impl;

import com.furniture.api.dto.request.CreateOrderRequest;
import com.furniture.api.dto.response.OrderResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.exception.ResourceNotFoundException;
import com.furniture.api.model.*;
import com.furniture.api.model.ReturnRequest;
import com.furniture.api.repository.*;
import com.furniture.api.service.CartService;
import com.furniture.api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartService cartService;
    private final ReturnRequestRepository returnRequestRepository;

    private static final BigDecimal DEFAULT_SHIPPING_FEE = BigDecimal.ZERO;

    @Override
    @Transactional
    public OrderResponse createOrder(Integer userId, CreateOrderRequest request) {
        Address address = resolveShippingAddress(userId, request);

        // Get items to order
        List<CartItem> itemsToOrder;
        if (Boolean.TRUE.equals(request.getFromCart())) {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Cart", "userId", userId));
            itemsToOrder = cartItemRepository.findByCartId(cart.getCartId());
            if (itemsToOrder.isEmpty()) {
                throw new BadRequestException("Gio hang dang trong");
            }
        } else {
            itemsToOrder = convertRequestItemsToCartItems(request.getItems());
        }

        // H2: Validate stock before creating order
        for (CartItem item : itemsToOrder) {
            Product productForOrder = productRepository.findByProductIdAndStatus(
                    item.getProductId(), Product.ProductStatus.ACTIVE)
                    .orElseThrow(() -> new BadRequestException("San pham khong ton tai hoac da bi an"));
            if (item.getProductVariantId() != null) {
                ProductVariant variant = productVariantRepository.findById(item.getProductVariantId())
                        .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại hoặc đã bị xóa"));
                if (safeStock(variant.getStock()) < item.getQuantity()) {
                    throw new BadRequestException("Sản phẩm '" +
                            (item.getVariantInfo() != null ? item.getVariantInfo() : "#" + item.getProductId()) +
                            "' không đủ hàng (còn " + variant.getStock() + ", cần " + item.getQuantity() + ")");
                }
            } else {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));
                if (safeStock(product.getStock()) < item.getQuantity()) {
                    throw new BadRequestException("Sản phẩm '" + product.getProductName() +
                            "' không đủ hàng (còn " + product.getStock() + ", cần " + item.getQuantity() + ")");
                }
            }
        }

        // Calculate totals
        BigDecimal subtotal = itemsToOrder.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalShippingFee = DEFAULT_SHIPPING_FEE;

        // Create main order
        Order order = Order.builder()
                .userId(userId)
                .shippingAddressId(address.getAddressId())
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getPhone())
                .shippingAddressText(address.getFullAddress())
                .totalPrice(subtotal.add(totalShippingFee))
                .shippingFee(totalShippingFee)
                .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .note(request.getNote())
                .build();

        order = orderRepository.save(order);

        List<OrderItem> allOrderItems = new ArrayList<>();
        for (CartItem cartItem : itemsToOrder) {
            OrderItem orderItem = OrderItem.builder()
                    .orderId(order.getOrderId())
                    .productId(cartItem.getProductId())
                    .variantId(cartItem.getProductVariantId())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .discount(BigDecimal.ZERO)
                    .total(cartItem.getTotalPrice())
                    .variantInfo(cartItem.getVariantInfo())
                    .build();

            orderItem = orderItemRepository.save(orderItem);
            allOrderItems.add(orderItem);

            if (cartItem.getProductVariantId() != null) {
                productVariantRepository.findById(cartItem.getProductVariantId()).ifPresent(v -> {
                    v.setStock(Math.max(0, safeStock(v.getStock()) - cartItem.getQuantity()));
                    productVariantRepository.save(v);
                });
            }
            productRepository.findById(cartItem.getProductId()).ifPresent(p -> {
                p.setStock(Math.max(0, safeStock(p.getStock()) - cartItem.getQuantity()));
                productRepository.save(p);
            });
        }

        // Clear cart if ordered from cart
        if (Boolean.TRUE.equals(request.getFromCart())) {
            cartService.clearCart(userId);
        }

        return mapToOrderResponse(order, address, allOrderItems);
    }

    @Override
    @Transactional
    public OrderResponse getOrderById(Integer userId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Don hang khong thuoc tai khoan nay");
        }

        Address address = findOrderAddress(order);
        List<OrderItem> items = getOrderItems(orderId);

        return mapToOrderResponse(order, address, items);
    }

    @Override
    @Transactional
    public Page<OrderResponse> getUserOrders(Integer userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        List<Order> content = orders.getContent();
        if (content.isEmpty()) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, orders.getTotalElements());
        }

        // H3: Batch load addresses (1 query instead of N)
        Set<Integer> addressIds = content.stream()
                .map(Order::getShippingAddressId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Integer, Address> addressMap = addressRepository.findAllById(addressIds).stream()
                .collect(Collectors.toMap(Address::getAddressId, Function.identity()));

        // H3: Batch load returnStatus (2 queries instead of 2N)
        Set<Integer> orderIds = content.stream()
                .map(Order::getOrderId)
                .collect(Collectors.toSet());
        Set<Integer> approvedReturnIds = returnRequestRepository
                .findOrderIdsByOrderIdsAndStatus(orderIds, ReturnRequest.ReturnStatus.APPROVED);
        Set<Integer> pendingReturnIds = returnRequestRepository
                .findOrderIdsByOrderIdsAndStatus(orderIds, ReturnRequest.ReturnStatus.PENDING);

        List<OrderResponse> responses = content.stream()
                .map(order -> {
                    Address address = addressMap.get(order.getShippingAddressId());
                    List<OrderItem> items = getOrderItems(order.getOrderId());
                    String returnStatus = approvedReturnIds.contains(order.getOrderId()) ? "APPROVED"
                            : pendingReturnIds.contains(order.getOrderId()) ? "PENDING" : null;
                    return mapToOrderResponse(order, address, items, returnStatus);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, orders.getTotalElements());
    }

    @Override
    @Transactional
    public Page<OrderResponse> getUserOrdersByStatus(Integer userId, String status, Pageable pageable) {
        Order.OrderStatus orderStatus;
        try {
            orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        Page<Order> orders = orderRepository.findByUserIdAndStatus(userId, orderStatus, pageable);

        List<Order> content = orders.getContent();
        if (content.isEmpty()) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, orders.getTotalElements());
        }
        Set<Integer> addressIds = content.stream().map(Order::getShippingAddressId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, Address> addressMap = addressRepository.findAllById(addressIds).stream()
                .collect(Collectors.toMap(Address::getAddressId, Function.identity()));
        Set<Integer> orderIds = content.stream().map(Order::getOrderId).collect(Collectors.toSet());
        Set<Integer> approvedReturnIds = returnRequestRepository
                .findOrderIdsByOrderIdsAndStatus(orderIds, ReturnRequest.ReturnStatus.APPROVED);
        Set<Integer> pendingReturnIds = returnRequestRepository
                .findOrderIdsByOrderIdsAndStatus(orderIds, ReturnRequest.ReturnStatus.PENDING);

        List<OrderResponse> responses = content.stream()
                .map(order -> {
                    Address address = addressMap.get(order.getShippingAddressId());
                    List<OrderItem> items = getOrderItems(order.getOrderId());
                    String rs = approvedReturnIds.contains(order.getOrderId()) ? "APPROVED"
                            : pendingReturnIds.contains(order.getOrderId()) ? "PENDING" : null;
                    return mapToOrderResponse(order, address, items, rs);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, orders.getTotalElements());
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Integer userId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Don hang khong thuoc tai khoan nay");
        }

        // H1: Allow cancel for both PENDING and PROCESSING (match Android UI)
        if (order.getStatus() != Order.OrderStatus.PENDING
                && order.getStatus() != Order.OrderStatus.PROCESSING) {
            throw new BadRequestException("Chỉ có thể hủy đơn hàng đang chờ xác nhận hoặc đang xử lý");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setPaymentStatus(Order.PaymentStatus.CANCELLED);
        order = orderRepository.save(order);

        // Restore stock for cancelled order items.
        List<OrderItem> items = getOrderItems(orderId);
        for (OrderItem item : items) {
            if (item.getVariantId() != null) {
                productVariantRepository.findById(item.getVariantId()).ifPresent(v -> {
                    v.setStock(safeStock(v.getStock()) + item.getQuantity());
                    productVariantRepository.save(v);
                });
            }
            productRepository.findById(item.getProductId()).ifPresent(p -> {
                p.setStock(safeStock(p.getStock()) + item.getQuantity());
                productRepository.save(p);
            });
        }

        Address address = findOrderAddress(order);

        return mapToOrderResponse(order, address, items);
    }

    @Override
    @Transactional
    public OrderResponse confirmReceived(Integer userId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Don hang khong thuoc tai khoan nay");
        }

        if (order.getStatus() != Order.OrderStatus.SHIPPED) {
            throw new BadRequestException("Chỉ có thể xác nhận nhận hàng khi đơn đang giao");
        }

        order.setStatus(Order.OrderStatus.DELIVERED);
        order.setPaymentStatus(Order.PaymentStatus.PAID);
        order = orderRepository.save(order);

        Address address = findOrderAddress(order);
        List<OrderItem> items = getOrderItems(orderId);
        return mapToOrderResponse(order, address, items);
    }

    private Address findOrderAddress(Order order) {
        if (order.getShippingAddressId() == null) {
            return null;
        }
        return addressRepository.findById(order.getShippingAddressId()).orElse(null);
    }

    private Address resolveShippingAddress(Integer userId, CreateOrderRequest request) {
        if (request.getAddressId() == null) {
            throw new BadRequestException("Vui long chon dia chi giao hang");
        }
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getAddressId()));
        if (!address.getUserId().equals(userId)) {
            throw new BadRequestException("Dia chi giao hang khong thuoc tai khoan nay");
        }
        return address;
    }

    private List<CartItem> convertRequestItemsToCartItems(List<CreateOrderRequest.OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new BadRequestException("Danh sach san pham khong duoc trong");
        }

        List<CartItem> cartItems = new ArrayList<>();
        for (CreateOrderRequest.OrderItemRequest item : items) {
            Product product = productRepository.findByProductIdAndStatus(item.getProductId(), Product.ProductStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", item.getProductId()));

            ProductVariant variant = null;
            if (item.getVariantId() != null) {
                variant = productVariantRepository.findById(item.getVariantId())
                        .orElseThrow(() -> new BadRequestException("Phân loại sản phẩm không tồn tại"));
                if (!product.getProductId().equals(variant.getProductId())) {
                    throw new BadRequestException("Phân loại sản phẩm không thuộc sản phẩm đã chọn");
                }
            }

            // Get price from variant or first variant of product
            BigDecimal price;
            if (variant != null) {
                price = variant.getPrice();
            } else {
                List<ProductVariant> variants = productVariantRepository.findByProductId(product.getProductId());
                if (variants.isEmpty()) {
                    throw new BadRequestException("San pham chua co thong tin gia");
                }
                variant = variants.get(0);
                price = variant.getPrice();
                item.setVariantId(variant.getVariantId());
            }

            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;
            if (quantity < 1) {
                throw new BadRequestException("Số lượng sản phẩm phải lớn hơn 0");
            }
            if (safeStock(variant.getStock()) < quantity) {
                throw new BadRequestException("Sản phẩm không đủ hàng");
            }

            CartItem cartItem = CartItem.builder()
                    .productId(product.getProductId())
                    .productVariantId(item.getVariantId())
                    .quantity(quantity)
                    .price(price)
                    .totalPrice(price.multiply(BigDecimal.valueOf(quantity)))
                    .variantInfo(variant != null ? getVariantInfo(variant) : null)
                    .build();

            cartItems.add(cartItem);
        }

        return cartItems;
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

    private int safeStock(Integer stock) {
        return stock != null ? stock : 0;
    }

    private List<OrderItem> getOrderItems(Integer orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    private Order.PaymentMethod parsePaymentMethod(String method) {
        if (method == null || method.isBlank()) {
            return Order.PaymentMethod.COD;
        }

        String normalized = method.trim().toUpperCase();
        if ("BANK_TRANSFER".equals(normalized)) {
            return Order.PaymentMethod.BANK_TRANSFER;
        }
        return Order.PaymentMethod.COD;
    }

    /** Single-order mapping — computes returnStatus with individual DB queries. */
    private OrderResponse mapToOrderResponse(Order order, Address address, List<OrderItem> items) {
        String returnStatus = null;
        if (returnRequestRepository.existsByOrderIdAndStatus(order.getOrderId(), ReturnRequest.ReturnStatus.APPROVED)) {
            returnStatus = "APPROVED";
        } else if (returnRequestRepository.existsByOrderIdAndStatus(order.getOrderId(), ReturnRequest.ReturnStatus.PENDING)) {
            returnStatus = "PENDING";
        }
        return mapToOrderResponse(order, address, items, returnStatus);
    }

    /** Batch-optimised mapping — accepts pre-computed returnStatus from batch queries. */
    private OrderResponse mapToOrderResponse(Order order, Address address, List<OrderItem> items, String precomputedReturnStatus) {
        List<OrderResponse.OrderItemResponse> itemResponses = items.stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(OrderItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingFee = order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO;

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .orderCode("ORD" + String.format("%08d", order.getOrderId()))
                .userId(order.getUserId())
                .recipientName(order.getRecipientName() != null ? order.getRecipientName() : address != null ? address.getRecipientName() : null)
                .recipientPhone(order.getRecipientPhone() != null ? order.getRecipientPhone() : address != null ? address.getPhone() : null)
                .shippingAddress(order.getShippingAddressText() != null ? order.getShippingAddressText() : address != null ? address.getFullAddress() : null)
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .totalAmount(subtotal.add(shippingFee))
                .paymentMethod(order.getPaymentMethod().name())
                .paymentStatus(order.getPaymentStatus().name())
                .orderStatus(order.getStatus().name())
                .returnStatus(precomputedReturnStatus)
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    private OrderResponse.OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        Product product = productRepository.findById(item.getProductId()).orElse(null);
        ProductVariant variant = item.getVariantId() != null ?
                productVariantRepository.findById(item.getVariantId()).orElse(null) : null;

        String productImage = null;
        if (variant != null && variant.getImageUrl() != null) {
            productImage = variant.getImageUrl();
        } else if (product != null && product.getVariants() != null && !product.getVariants().isEmpty()) {
            productImage = product.getVariants().get(0).getImageUrl();
        }

        return OrderResponse.OrderItemResponse.builder()
                .orderItemId(item.getOrderItemId())
                .productId(item.getProductId())
                .productName(product != null ? product.getProductName() : "Unknown")
                .productImage(productImage)
                .variantId(item.getVariantId())
                .variantName(item.getVariantInfo())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getTotal())
                .build();
    }
}
