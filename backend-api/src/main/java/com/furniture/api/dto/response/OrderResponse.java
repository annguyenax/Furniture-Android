package com.furniture.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Integer orderId;
    private String orderCode;
    private Integer userId;
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String returnStatus;
    private String note;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Integer orderItemId;
        private Integer productId;
        private String productName;
        private String productImage;
        private Integer variantId;
        private String variantName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
