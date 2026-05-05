package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.request.CreateOrderRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApi {

    @GET("orders")
    Call<ApiResponse<PageResponse<Order>>> getOrders(
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("orders/{orderId}")
    Call<ApiResponse<Order>> getOrderById(@Path("orderId") Integer orderId);

    @GET("orders/status/{status}")
    Call<ApiResponse<PageResponse<Order>>> getOrdersByStatus(
            @Path("status") String status,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("orders")
    Call<ApiResponse<Order>> createOrder(@Body CreateOrderRequest request);

    @POST("orders/{orderId}/cancel")
    Call<ApiResponse<Order>> cancelOrder(@Path("orderId") Integer orderId);
}
