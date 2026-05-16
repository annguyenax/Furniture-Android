package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Product API endpoints
 */
public interface ProductApi {

    @GET("products")
    Call<ApiResponse<PageResponse<Product>>> getAllProducts(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir
    );

    @GET("products/{productId}")
    Call<ApiResponse<Product>> getProductById(@Path("productId") int productId);

    @GET("products/category/{categoryId}")
    Call<ApiResponse<PageResponse<Product>>> getProductsByCategory(
            @Path("categoryId") int categoryId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("products/search")
    Call<ApiResponse<PageResponse<Product>>> searchProducts(
            @Query("keyword") String keyword,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("products/shop/{shopId}")
    Call<ApiResponse<PageResponse<Product>>> getProductsByShop(
            @Path("shopId") int shopId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("products/featured")
    Call<ApiResponse<List<Product>>> getFeaturedProducts(@Query("limit") int limit);

    @GET("products/new-arrivals")
    Call<ApiResponse<List<Product>>> getNewArrivals(@Query("limit") int limit);

    @GET("products/best-deals")
    Call<ApiResponse<List<Product>>> getBestDeals(@Query("limit") int limit);
}
