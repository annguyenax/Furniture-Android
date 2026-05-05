package com.furniture.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Integer cartId;
    private Integer userId;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private List<CartItemResponse> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemResponse {
        private Integer cartItemId;
        private Integer productId;
        private String productName;
        private String productImage;
        private Integer variantId;
        private String variantName;
        private Integer shopId;
        private String shopName;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
        private Integer stock;
    }
}
