package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Product;

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

    class CreateProductRequest {
        private String productName;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private BigDecimal discount;
        private Integer categoryId;

        public CreateProductRequest(String productName, String description, BigDecimal price,
                                    int stock, BigDecimal discount, Integer categoryId) {
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.stock = stock;
            this.discount = discount;
            this.categoryId = categoryId;
        }

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public BigDecimal getPrice() { return price; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public Integer getCategoryId() { return categoryId; }
    }

    class UpdateProductRequest {
        private String productName;
        private String description;
        private Integer stock;
        private BigDecimal discount;
        private String status;

        public UpdateProductRequest(String productName, String description,
                                    int stock, BigDecimal discount, String status) {
            this.productName = productName;
            this.description = description;
            this.stock = stock;
            this.discount = discount;
            this.status = status;
        }

        public String getProductName() { return productName; }
        public String getDescription() { return description; }
        public Integer getStock() { return stock; }
        public BigDecimal getDiscount() { return discount; }
        public String getStatus() { return status; }
    }
}
