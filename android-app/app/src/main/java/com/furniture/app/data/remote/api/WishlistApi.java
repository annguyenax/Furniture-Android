package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.WishlistItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WishlistApi {

    @GET("wishlist")
    Call<ApiResponse<List<WishlistItem>>> getWishlist();

    @POST("wishlist/{productId}")
    Call<ApiResponse<Void>> addToWishlist(@Path("productId") int productId);

    @DELETE("wishlist/{productId}")
    Call<ApiResponse<Void>> removeFromWishlist(@Path("productId") int productId);

    @GET("wishlist/check/{productId}")
    Call<ApiResponse<Boolean>> checkWishlist(@Path("productId") int productId);
}
