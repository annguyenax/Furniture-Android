package com.furniture.api.dto.response;

import com.furniture.api.model.Product;
import com.furniture.api.model.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Integer productId;
    private String productName;
    private String description;
    private BigDecimal discount;
    private Integer stock;
    private Integer sold;
    private BigDecimal weight;
    private String dimensions;
    private String status;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private Integer shopId;
    private String shopName;
    private Integer categoryId;
    private String categoryName;
    private List<VariantResponse> variants;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResponse {
        private Integer variantId;
        private String size;
        private String color;
        private String material;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;
    }

    public static ProductResponse fromEntity(Product product) {
        ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
            .productId(product.getProductId())
            .productName(product.getProductName())
            .description(product.getDescription())
            .discount(product.getDiscount())
            .stock(product.getStock())
            .sold(product.getSold())
            .weight(product.getWeight())
            .dimensions(product.getDimensions())
            .status(product.getStatus() != null ? product.getStatus().name() : null)
            .averageRating(product.getAverageRating())
            .reviewCount(product.getReviewCount())
            .shopId(product.getShopId())
            .categoryId(product.getCategoryId())
            .createdAt(product.getCreatedAt());

        if (product.getShop() != null) {
            builder.shopName(product.getShop().getShopName());
        }

        if (product.getCategory() != null) {
            builder.categoryName(product.getCategory().getCategoryName());
        }

        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            builder.variants(product.getVariants().stream()
                .map(ProductResponse::variantFromEntity)
                .collect(Collectors.toList()));
        }

        return builder.build();
    }

    private static VariantResponse variantFromEntity(ProductVariant variant) {
        return VariantResponse.builder()
            .variantId(variant.getVariantId())
            .size(variant.getSize())
            .color(variant.getColor())
            .material(variant.getMaterial())
            .price(variant.getPrice())
            .stock(variant.getStock())
            .imageUrl(variant.getImageUrl())
            .build();
    }
}
