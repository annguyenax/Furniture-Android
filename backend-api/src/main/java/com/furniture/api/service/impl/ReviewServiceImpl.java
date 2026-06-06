package com.furniture.api.service.impl;

import com.furniture.api.controller.ReviewController.ReviewRequest;
import com.furniture.api.dto.response.ReviewResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.model.ProductReview;
import com.furniture.api.repository.ProductReviewRepository;
import com.furniture.api.repository.UserRepository;
import com.furniture.api.service.CloudinaryService;
import com.furniture.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ProductReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Integer userId) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Điểm đánh giá từ 1 đến 5 sao");
        }
        if (request.getProductId() == null) {
            throw new BadRequestException("Thiếu mã sản phẩm");
        }
        if (request.getOrderId() != null &&
                reviewRepository.existsByProductIdAndUserIdAndOrderId(
                        request.getProductId(), userId, request.getOrderId())) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này cho đơn hàng này rồi");
        }

        ProductReview review = ProductReview.builder()
                .productId(request.getProductId())
                .userId(userId)
                .orderId(request.getOrderId())
                .rating(request.getRating())
                .comment(request.getComment())
                .images(request.getImages())
                .isVerified(true)
                .build();

        review = reviewRepository.save(review);
        return ReviewResponse.fromEntity(review, resolveUserName(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasReviewed(Integer productId, Integer orderId, Integer userId) {
        return orderId != null
                ? reviewRepository.existsByProductIdAndUserIdAndOrderId(productId, userId, orderId)
                : reviewRepository.existsByProductIdAndUserId(productId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getReviewedProductsForOrder(Integer orderId, Integer userId) {
        return reviewRepository.findProductIdsByUserIdAndOrderId(userId, orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getFullyReviewedOrders(Integer userId) {
        return reviewRepository.findFullyReviewedOrderIds(userId);
    }

    @Override
    public String uploadReviewImage(MultipartFile file) {
        CloudinaryService.UploadResult result = cloudinaryService.uploadImage(file, "furniture/reviews");
        return result.url();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(Integer productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(r -> ReviewResponse.fromEntity(r, resolveUserName(r.getUserId())));
    }

    private String resolveUserName(Integer userId) {
        return userRepository.findById(userId).map(u -> {
            String first = u.getFirstName() != null ? u.getFirstName() : "";
            String last = u.getLastName() != null ? u.getLastName() : "";
            String full = (first + " " + last).trim();
            return !full.isEmpty() ? full : (u.getUsername() != null ? u.getUsername() : "Ẩn danh");
        }).orElse("Người dùng");
    }
}
