package com.furniture.api.controller;

import com.furniture.api.dto.request.ReviewRequest;
import com.furniture.api.dto.response.ApiResponse;
import com.furniture.api.dto.response.ReviewResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @RequestBody ReviewRequest request, Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        try {
            ReviewResponse response = reviewService.createReview(request, userId);
            return ResponseEntity.ok(ApiResponse.success("Đã gửi đánh giá", response));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> hasReviewed(
            @PathVariable Integer productId,
            @RequestParam(required = false) Integer orderId,
            Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(reviewService.hasReviewed(productId, orderId, userId)));
    }

    @GetMapping("/check-order/{orderId}")
    public ResponseEntity<ApiResponse<java.util.List<Integer>>> getReviewedProductsForOrder(
            @PathVariable Integer orderId, Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(reviewService.getReviewedProductsForOrder(orderId, userId)));
    }

    @GetMapping("/fully-reviewed-orders")
    public ResponseEntity<ApiResponse<java.util.List<Integer>>> getFullyReviewedOrders(Authentication auth) {
        Integer userId = Integer.parseInt(auth.getName());
        return ResponseEntity.ok(ApiResponse.success(reviewService.getFullyReviewedOrders(userId)));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<ApiResponse<String>> uploadReviewImage(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("Image uploaded", reviewService.uploadReviewImage(file)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getProductReviews(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(reviewService.getProductReviews(
                productId, PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

}
