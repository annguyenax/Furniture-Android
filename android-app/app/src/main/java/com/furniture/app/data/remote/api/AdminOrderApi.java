package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.Order;
import com.furniture.app.data.model.PageResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminOrderApi {

    @GET("admin/orders")
    Call<ApiResponse<PageResponse<Order>>> getAllOrders(
            @Query("page") int page,
            @Query("size") int size,
            @Query("status") String status,
            @Query("search") String search
    );

    @PUT("admin/orders/{orderId}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Path("orderId") Integer orderId,
            @Query("status") String status
    );
}
