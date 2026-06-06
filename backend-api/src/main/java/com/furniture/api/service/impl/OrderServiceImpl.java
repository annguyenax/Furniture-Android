package com.furniture.api.service.impl;

import com.furniture.api.dto.request.CreateOrderRequest;
import com.furniture.api.dto.response.OrderResponse;
import com.furniture.api.exception.BadRequestException;
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
    private final SubOrderRepository subOrderRepository;
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
        // Create or find address
        Address address = createOrFindAddress(userId, request);

        // Get items to order
        List<CartItem> itemsToOrder;
        if (Boolean.TRUE.equals(request.getFromCart())) {
            Cart cart = cartRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));
            itemsToOrder = cartItemRepository.findByCartId(cart.getCartId());
            if (itemsToOrder.isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }
        } else {
            itemsToOrder = convertRequestItemsToCartItems(request.getItems());
        }

        // H2: Validate stock before creating order
        for (CartItem item : itemsToOrder) {
            if (item.getProductVariantId() != null) {
                ProductVariant variant = productVariantRepository.findById(item.getProductVariantId())
                        .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại hoặc đã bị xóa"));
                if (variant.getStock() < item.getQuantity()) {
                    throw new BadRequestException("Sản phẩm '" +
                            (item.getVariantInfo() != null ? item.getVariantInfo() : "#" + item.getProductId()) +
                            "' không đủ hàng (còn " + variant.getStock() + ", cần " + item.getQuantity() + ")");
                }
            } else {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new BadRequestException("Sản phẩm không tồn tại"));
                if (product.getStock() != null && product.getStock() < item.getQuantity()) {
                    throw new BadRequestException("Sản phẩm '" + product.getProductName() +
                            "' không đủ hàng (còn " + product.getStock() + ", cần " + item.getQuantity() + ")");
                }
            }
        }

        // Calculate totals
        BigDecimal subtotal = itemsToOrder.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group items by shop
        Map<Integer, List<CartItem>> itemsByShop = itemsToOrder.stream()
                .collect(Collectors.groupingBy(CartItem::getShopId));

        BigDecimal totalShippingFee = DEFAULT_SHIPPING_FEE.multiply(BigDecimal.valueOf(itemsByShop.size()));

        // Create main order
        Order order = Order.builder()
                .userId(userId)
                .shippingAddressId(address.getAddressId())
                .totalPrice(subtotal.add(totalShippingFee))
                .shippingFee(totalShippingFee)
                .paymentMethod(parsePaymentMethod(request.getPaymentMethod()))
                .status(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .note(request.getNote())
                .build();

        order = orderRepository.save(order);

        // Create sub-orders for each shop
        List<OrderItem> allOrderItems = new ArrayList<>();
        for (Map.Entry<Integer, List<CartItem>> entry : itemsByShop.entrySet()) {
            Integer shopId = entry.getKey();
            List<CartItem> shopItems = entry.getValue();

            BigDecimal shopSubtotal = shopItems.stream()
                    .map(CartItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            SubOrder subOrder = SubOrder.builder()
                    .orderId(order.getOrderId())
                    .shopId(shopId)
                    .totalPrice(shopSubtotal)
                    .shippingFee(DEFAULT_SHIPPING_FEE)
                    .status(SubOrder.SubOrderStatus.PENDING)
                    .build();

            subOrder = subOrderRepository.save(subOrder);

            // Create order items and reduce stock
            for (CartItem cartItem : shopItems) {
                OrderItem orderItem = OrderItem.builder()
                        .subOrderId(subOrder.getSubOrderId())
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

                // Reduce stock on variant first, then on product
                if (cartItem.getProductVariantId() != null) {
                    productVariantRepository.findById(cartItem.getProductVariantId()).ifPresent(v -> {
                        v.setStock(Math.max(0, v.getStock() - cartItem.getQuantity()));
                        productVariantRepository.save(v);
                    });
                }
                productRepository.findById(cartItem.getProductId()).ifPresent(p -> {
                    p.setStock(Math.max(0, p.getStock() - cartItem.getQuantity()));
                    productRepository.save(p);
                });
            }
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
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        Address address = addressRepository.findById(order.getShippingAddressId()).orElse(null);
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
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        // H1: Allow cancel for both PENDING and PROCESSING (match Android UI)
        if (order.getStatus() != Order.OrderStatus.PENDING
                && order.getStatus() != Order.OrderStatus.PROCESSING) {
            throw new BadRequestException("Chỉ có thể hủy đơn hàng đang chờ xác nhận hoặc đang xử lý");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setPaymentStatus(Order.PaymentStatus.CANCELLED);
        order = orderRepository.save(order);

        // Cancel sub-orders and restore stock
        List<SubOrder> subOrders = subOrderRepository.findByOrderId(orderId);
        for (SubOrder subOrder : subOrders) {
            subOrder.setStatus(SubOrder.SubOrderStatus.CANCELLED);
            subOrderRepository.save(subOrder);
        }

        List<OrderItem> items = getOrderItems(orderId);
        for (OrderItem item : items) {
            if (item.getVariantId() != null) {
                productVariantRepository.findById(item.getVariantId()).ifPresent(v -> {
                    v.setStock(v.getStock() + item.getQuantity());
                    productVariantRepository.save(v);
                });
            }
            productRepository.findById(item.getProductId()).ifPresent(p -> {
                p.setStock(p.getStock() + item.getQuantity());
                productRepository.save(p);
            });
        }

        Address address = addressRepository.findById(order.getShippingAddressId()).orElse(null);

        return mapToOrderResponse(order, address, items);
    }

    private Address createOrFindAddress(Integer userId, CreateOrderRequest request) {
        // Create a new address for this order
        Address address = Address.builder()
                .userId(userId)
                .recipientName(request.getRecipientName())
                .phone(request.getRecipientPhone())
                .addressLine(request.getShippingAddress())
                .city("Vietnam")
                .district("District")
                .ward("Ward")
                .isDefault(false)
                .build();

        return addressRepository.save(address);
    }

    private List<CartItem> convertRequestItemsToCartItems(List<CreateOrderRequest.OrderItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("No items provided");
        }

        List<CartItem> cartItems = new ArrayList<>();
        for (CreateOrderRequest.OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            ProductVariant variant = null;
            if (item.getVariantId() != null) {
                variant = productVariantRepository.findById(item.getVariantId()).orElse(null);
            }

            // Get price from variant or first variant of product
            BigDecimal price;
            if (variant != null) {
                price = variant.getPrice();
            } else if (product.getVariants() != null && !product.getVariants().isEmpty()) {
                price = product.getVariants().get(0).getPrice();
            } else {
                throw new RuntimeException("Product has no price information");
            }
            int quantity = item.getQuantity() != null ? item.getQuantity() : 1;

            CartItem cartItem = CartItem.builder()
                    .productId(product.getProductId())
                    .productVariantId(item.getVariantId())
                    .shopId(product.getShopId())
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

    private List<OrderItem> getOrderItems(Integer orderId) {
        List<SubOrder> subOrders = subOrderRepository.findByOrderId(orderId);
        List<OrderItem> items = new ArrayList<>();
        for (SubOrder subOrder : subOrders) {
            items.addAll(orderItemRepository.findBySubOrderId(subOrder.getSubOrderId()));
        }
        return items;
    }

    private Order.PaymentMethod parsePaymentMethod(String method) {
        if (method == null) {
            return Order.PaymentMethod.COD;
        }
        try {
            return Order.PaymentMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Order.PaymentMethod.COD;
        }
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
                .recipientName(address != null ? address.getRecipientName() : null)
                .recipientPhone(address != null ? address.getPhone() : null)
                .shippingAddress(address != null ? address.getFullAddress() : null)
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
