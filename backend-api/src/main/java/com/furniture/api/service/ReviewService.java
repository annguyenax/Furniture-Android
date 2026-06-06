package com.furniture.api.service;

import com.furniture.api.controller.ReviewController.ReviewRequest;
import com.furniture.api.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(ReviewRequest request, Integer userId);

    boolean hasReviewed(Integer productId, Integer orderId, Integer userId);

    List<Integer> getReviewedProductsForOrder(Integer orderId, Integer userId);

    List<Integer> getFullyReviewedOrders(Integer userId);

    String uploadReviewImage(MultipartFile file);

    Page<ReviewResponse> getProductReviews(Integer productId, Pageable pageable);
}
