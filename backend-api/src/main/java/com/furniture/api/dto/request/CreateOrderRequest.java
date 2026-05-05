package com.furniture.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String paymentMethod; // COD, VNPAY, MOMO
    private String note;
    private Boolean fromCart; // If true, create order from cart items
    private List<OrderItemRequest> items; // If fromCart is false, use these items

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        private Integer productId;
        private Integer variantId;
        private Integer quantity;
    }
}
