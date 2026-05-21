package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Product;
import com.furniture.app.data.model.ProductVariant;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AdminProductApi {

    @POST("admin/products")
    Call<ApiResponse<Product>> createProduct(@Body CreateProductRequest request);

    @PUT("admin/products/{productId}")
    Call<ApiResponse<Product>> updateProduct(
            @Path("productId") int productId,
            @Body UpdateProductRequest request
    );

    @DELETE("admin/products/{productId}")
    Call<ApiResponse<Void>> deleteProduct(@Path("productId") int productId);

    @POST("admin/products/{productId}/variants")
    Call<ApiResponse<ProductVariant>> createVariant(
            @Path("productId") int productId,
            @Body VariantRequest request);

    @PUT("admin/variants/{variantId}")
    Call<ApiResponse<ProductVariant>> updateVariant(
            @Path("variantId") int variantId,
            @Body VariantRequest request);

    @DELETE("admin/variants/{variantId}")
    Call<ApiResponse<Void>> deleteVariant(@Path("variantId") int variantId);

    class CreateProductRequest {
        private String productName;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private BigDecimal discount;
        private Integer categoryId;
        private String imageUrl;

        public CreateProductRequest(String productName, String description, BigDecimal price,
                                    int stock, BigDecimal discount, Integer categoryId, String imageUrl) {
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.stock = stock;
            this.discount = discount;
            this.categoryId = categoryId;
            this.imageUrl = imageUrl;
        }

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public BigDecimal getPrice() { return price; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public Integer getCategoryId() { return categoryId; }
        public String getImageUrl() { return imageUrl; }
    }

    class UpdateProductRequest {
        private String productName;
        private String description;
        private Integer stock;
        private BigDecimal discount;
        private String status;
        private String imageUrl;

        public UpdateProductRequest(String productName, String description,
                                    int stock, BigDecimal discount, String status, String imageUrl) {
            this.productName = productName;
            this.description = description;
            this.stock = stock;
            this.discount = discount;
            this.status = status;
            this.imageUrl = imageUrl;
        }

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public String getStatus() { return status; }
        public String getImageUrl() { return imageUrl; }
    }

    class VariantRequest {
        private String size;
        private String color;
        private String material;
        private BigDecimal price;
        private Integer stock;
        private String imageUrl;

        public VariantRequest(String size, String color, String material,
                              BigDecimal price, Integer stock, String imageUrl) {
            this.size = size;
            this.color = color;
            this.material = material;
            this.price = price;
            this.stock = stock;
            this.imageUrl = imageUrl;
        }

        public String getSize() { return size; }
        public String getColor() { return color; }
        public String getMaterial() { return material; }
        public BigDecimal getPrice() { return price; }
        public Integer getStock() { return stock; }
        public String getImageUrl() { return imageUrl; }
    }
}
