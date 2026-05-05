package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Cart;
import com.furniture.app.data.model.request.AddToCartRequest;
import com.furniture.app.data.model.request.UpdateCartItemRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {

    @GET("cart")
    Call<ApiResponse<Cart>> getCart();

    @POST("cart/add")
    Call<ApiResponse<Cart>> addToCart(@Body AddToCartRequest request);

    @PUT("cart/items/{cartItemId}")
    Call<ApiResponse<Cart>> updateCartItem(
            @Path("cartItemId") Integer cartItemId,
            @Body UpdateCartItemRequest request
    );

    @DELETE("cart/items/{cartItemId}")
    Call<ApiResponse<Void>> removeCartItem(@Path("cartItemId") Integer cartItemId);

    @DELETE("cart/clear")
    Call<ApiResponse<Void>> clearCart();
}
