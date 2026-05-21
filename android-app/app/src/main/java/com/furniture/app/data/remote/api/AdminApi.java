package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.AdminReviewItem;
import com.furniture.app.data.model.AdminStatsResponse;
import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AdminApi {

    @GET("admin/stats")
    Call<ApiResponse<AdminStatsResponse>> getStats(@Query("period") String period);

    @GET("admin/reviews")
    Call<ApiResponse<PageResponse<AdminReviewItem>>> getReviews(
            @Query("page") int page,
            @Query("size") int size);

    @DELETE("admin/reviews/{id}")
    Call<ApiResponse<Void>> deleteReview(@Path("id") int id);
}
