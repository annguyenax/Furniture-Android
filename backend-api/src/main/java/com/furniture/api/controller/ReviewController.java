package com.furniture.api.controller;

import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.ReviewResponse;
import com.furniture.api.model.ProductReview;
import com.furniture.api.repository.ProductReviewRepository;
import com.furniture.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ProductReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestBody ReviewRequest request, Authentication auth) {

        Integer userId = Integer.parseInt(auth.getName());

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Điểm đánh giá từ 1 đến 5 sao"));
        }
        if (request.getProductId() == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Thiếu mã sản phẩm"));
        }
        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), userId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Bạn đã đánh giá sản phẩm này rồi"));
        }

        ProductReview review = ProductReview.builder()
                .productId(request.getProductId())
                .userId(userId)
                .rating(request.getRating())
                .comment(request.getComment())
                .images(request.getImages())
                .isVerified(true)
                .build();

        review = reviewRepository.save(review);
        return ResponseEntity.ok(ApiResponse.success("Đã gửi đánh giá",
                ReviewResponse.fromEntity(review, resolveUserName(userId))));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> hasReviewed(
            @PathVariable Integer productId, Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        boolean reviewed = reviewRepository.existsByProductIdAndUserId(productId, userId);
        return ResponseEntity.ok(ApiResponse.success(reviewed));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProductReviews(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ProductReview> reviews = reviewRepository.findByProductId(productId, pageable);
        Page<ReviewResponse> responses = reviews.map(r ->
                ReviewResponse.fromEntity(r, resolveUserName(r.getUserId())));
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    private String resolveUserName(Integer userId) {
        return userRepository.findById(userId).map(u -> {
            String first = u.getFirstName() != null ? u.getFirstName() : "";
            String last = u.getLastName() != null ? u.getLastName() : "";
            String full = (first + " " + last).trim();
            return !full.isEmpty() ? full : (u.getUsername() != null ? u.getUsername() : "Ẩn danh");
        }).orElse("Người dùng");
    }

    public static class ReviewRequest {
        private Integer productId;
        private Integer rating;
        private String comment;
        private String images;

        public Integer getProductId() { return productId; }
        public Integer getRating() { return rating; }
        public String getComment() { return comment; }
        public String getImages() { return images; }
    }
}
