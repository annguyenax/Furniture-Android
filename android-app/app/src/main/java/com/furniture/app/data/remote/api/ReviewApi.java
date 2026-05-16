package com.furniture.app.data.remote.api;

import com.furniture.app.data.model.ApiResponse;
import com.furniture.app.data.model.PageResponse;
import com.furniture.app.data.model.ReviewModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewApi {

    @POST("reviews")
    Call<ApiResponse<ReviewModel>> createReview(@Body ReviewRequest request);

    @GET("reviews/check/{productId}")
    Call<ApiResponse<Boolean>> hasReviewed(@Path("productId") int productId);

    @GET("reviews/product/{productId}")
    Call<ApiResponse<PageResponse<ReviewModel>>> getProductReviews(
            @Path("productId") int productId,
            @Query("page") int page,
            @Query("size") int size
    );

    class ReviewRequest {
        private final Integer productId;
        private final Integer rating;
        private final String comment;
        private final String images;

        public ReviewRequest(Integer productId, Integer rating, String comment, String images) {
            this.productId = productId;
            this.rating = rating;
            this.comment = comment;
            this.images = images;
        }

        public Integer getProductId() { return productId; }
        public Integer getRating() { return rating; }
        public String getComment() { return comment; }
        public String getImages() { return images; }
    }
}
