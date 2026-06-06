package com.furniture.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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
    
    @NotEmpty(message = "Danh sách sản phẩm không được trống")
    @Valid
    private List<OrderItemRequest> items; // If fromCart is false, use these items

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotNull(message = "ID sản phẩm không được để trống")
        private Integer productId;
        private Integer variantId;
        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng sản phẩm phải lớn hơn 0")
        private Integer quantity;
    }
}
