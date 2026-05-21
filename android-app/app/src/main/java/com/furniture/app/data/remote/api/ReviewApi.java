package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.ReviewModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewApi {

    @POST("reviews")
    Call<ApiResponse<ReviewModel>> createReview(@Body ReviewRequest request);

    @Multipart
    @POST("reviews/upload-image")
    Call<ApiResponse<String>> uploadReviewImage(@Part MultipartBody.Part file);

    @GET("reviews/check/{productId}")
    Call<ApiResponse<Boolean>> hasReviewed(@Path("productId") int productId);

    @GET("reviews/check-order/{orderId}")
    Call<ApiResponse<java.util.List<Integer>>> getReviewedProductsForOrder(@Path("orderId") int orderId);

    @GET("reviews/fully-reviewed-orders")
    Call<ApiResponse<java.util.List<Integer>>> getFullyReviewedOrders();

    @GET("reviews/product/{productId}")
    Call<ApiResponse<PageResponse<ReviewModel>>> getProductReviews(
            @Path("productId") int productId,
            @Query("page") int page,
            @Query("size") int size
    );

    class ReviewRequest {
        private final Integer productId;
        private final Integer orderId;
        private final Integer rating;
        private final String comment;
        private final String images;

        public ReviewRequest(Integer productId, Integer orderId, Integer rating, String comment, String images) {
            this.productId = productId;
            this.orderId = orderId;
            this.rating = rating;
            this.comment = comment;
            this.images = images;
        }

        public Integer getProductId() { return productId; }
        public Integer getOrderId() { return orderId; }
        public Integer getRating() { return rating; }
        public String getComment() { return comment; }
        public String getImages() { return images; }
    }
}
