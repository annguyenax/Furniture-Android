package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.ReturnRequestItem;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReturnRequestApi {

    @Multipart
    @POST("returns")
    Call<ApiResponse<ReturnRequestItem>> createReturn(
            @Part("orderId") RequestBody orderId,
            @Part("orderItemId") RequestBody orderItemId,
            @Part("reason") RequestBody reason,
            @Part MultipartBody.Part file);

    @GET("returns/check/{orderId}")
    Call<ApiResponse<String>> checkReturnStatus(@Path("orderId") int orderId);

    @GET("returns/my-returned-order-ids")
    Call<ApiResponse<java.util.List<Integer>>> getReturnedOrderIds();

    @GET("returns/my")
    Call<ApiResponse<PageResponse<ReturnRequestItem>>> getMyReturns(
            @Query("page") int page,
            @Query("size") int size);

    @GET("admin/returns")
    Call<ApiResponse<PageResponse<ReturnRequestItem>>> getAdminReturns(
            @Query("status") String status,
            @Query("page") int page,
            @Query("size") int size);

    @PUT("admin/returns/{id}/status")
    Call<ApiResponse<ReturnRequestItem>> updateReturnStatus(
            @Path("id") int id,
            @Query("status") String status,
            @Query("adminNote") String adminNote);
}
