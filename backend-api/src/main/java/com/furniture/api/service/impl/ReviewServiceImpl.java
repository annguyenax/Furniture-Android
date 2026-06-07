package com.furniture.api.service.impl;

import com.furniture.api.dto.request.ReviewRequest;
import com.furniture.api.dto.response.ReviewResponse;
import com.furniture.api.exception.BadRequestException;
import com.furniture.api.exception.ResourceNotFoundException;
import com.furniture.api.model.Order;
import com.furniture.api.model.ProductReview;
import com.furniture.api.repository.OrderItemRepository;
import com.furniture.api.repository.OrderRepository;
import com.furniture.api.repository.ProductReviewRepository;
import com.furniture.api.repository.ProductRepository;
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
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
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

        productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        boolean verifiedPurchase = false;
        if (request.getOrderId() != null) {
            validateReviewOrder(request, userId);
            verifiedPurchase = true;
        } else if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), userId)) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này rồi");
        }

        ProductReview review = ProductReview.builder()
                .productId(request.getProductId())
                .userId(userId)
                .orderId(request.getOrderId())
                .rating(request.getRating())
                .comment(request.getComment())
                .images(request.getImages())
                .isVerified(verifiedPurchase)
                .build();

        review = reviewRepository.save(review);
        return ReviewResponse.fromEntity(review, resolveUserName(userId));
    }

    private void validateReviewOrder(ReviewRequest request, Integer userId) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        if (!order.getUserId().equals(userId)) {
            throw new BadRequestException("Bạn không có quyền đánh giá đơn hàng này");
        }
        if (order.getStatus() != Order.OrderStatus.DELIVERED) {
            throw new BadRequestException("Chỉ có thể đánh giá sản phẩm trong đơn hàng đã giao");
        }
        if (!orderItemRepository.existsByOrderIdAndProductId(request.getOrderId(), request.getProductId())) {
            throw new BadRequestException("Sản phẩm không thuộc đơn hàng này");
        }
        if (reviewRepository.existsByProductIdAndUserIdAndOrderId(
                request.getProductId(), userId, request.getOrderId())) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này cho đơn hàng này rồi");
        }
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
        return reviewRepository.findReviewSummariesByProductId(productId, pageable)
                .map(r -> ReviewResponse.builder()
                        .reviewId(r.getReviewId())
                        .productId(r.getProductId())
                        .userId(r.getUserId())
                        .userName(r.getUserName())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .images(r.getImages())
                        .isVerified(r.getIsVerified())
                        .createdAt(r.getCreatedAt())
                        .build());
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
