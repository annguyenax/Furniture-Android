package com.furniture.api.dto.response;

import com.furniture.api.model.ProductReview;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private Integer reviewId;
    private Integer productId;
    private Integer userId;
    private String userName;
    private Integer rating;
    private String comment;
    private String images;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    public static ReviewResponse fromEntity(ProductReview r, String userName) {
        return ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .productId(r.getProductId())
                .userId(r.getUserId())
                .userName(userName)
                .rating(r.getRating())
                .comment(r.getComment())
                .images(r.getImages())
                .isVerified(r.getIsVerified())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
