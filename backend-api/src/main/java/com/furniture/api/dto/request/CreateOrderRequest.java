package com.furniture.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Vui long chon dia chi giao hang")
    private Integer addressId;
    private String paymentMethod; // COD, VNPAY, MOMO, BANK_TRANSFER
    private String note;
    private Boolean fromCart;

    @Valid
    private List<OrderItemRequest> items;

    @AssertTrue(message = "Danh sach san pham khong duoc trong")
    public boolean isCartOrHasItems() {
        return Boolean.TRUE.equals(fromCart) || (items != null && !items.isEmpty());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        @NotNull(message = "ID san pham khong duoc de trong")
        private Integer productId;
        private Integer variantId;

        @NotNull(message = "So luong khong duoc de trong")
        @Min(value = 1, message = "So luong san pham phai lon hon 0")
        private Integer quantity;
    }
}
