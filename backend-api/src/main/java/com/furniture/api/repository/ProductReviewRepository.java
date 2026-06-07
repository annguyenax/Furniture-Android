package com.furniture.api.repository;

import com.furniture.api.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    Page<ProductReview> findByProductId(Integer productId, Pageable pageable);

    @Query(
        nativeQuery = true,
        value = """
            SELECT
                r.review_id AS reviewId,
                r.product_id AS productId,
                r.user_id AS userId,
                COALESCE(NULLIF(TRIM(CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, ''))), ''), u.username, 'Ẩn danh') AS userName,
                r.rating AS rating,
                r.comment AS comment,
                r.images AS images,
                r.is_verified AS isVerified,
                r.created_at AS createdAt
            FROM product_reviews r
            LEFT JOIN users u ON u.user_id = r.user_id
            WHERE r.product_id = :productId
            ORDER BY r.created_at DESC
            """,
        countQuery = "SELECT COUNT(*) FROM product_reviews r WHERE r.product_id = :productId"
    )
    Page<ReviewSummaryProjection> findReviewSummariesByProductId(@Param("productId") Integer productId, Pageable pageable);

    List<ProductReview> findByUserId(Integer userId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") Integer productId);

    Long countByProductId(Integer productId);

    boolean existsByProductIdAndUserId(Integer productId, Integer userId);

    boolean existsByProductIdAndUserIdAndOrderId(Integer productId, Integer userId, Integer orderId);

    boolean existsByUserIdAndOrderId(Integer userId, Integer orderId);

    @Query("SELECT r.productId FROM ProductReview r WHERE r.userId = :userId AND r.orderId = :orderId")
    List<Integer> findProductIdsByUserIdAndOrderId(@Param("userId") Integer userId, @Param("orderId") Integer orderId);

    @Query(nativeQuery = true, value =
        "SELECT r.order_id FROM product_reviews r " +
        "WHERE r.user_id = :userId AND r.order_id IS NOT NULL " +
        "GROUP BY r.order_id " +
        "HAVING COUNT(DISTINCT r.product_id) >= (" +
        "  SELECT COUNT(DISTINCT oi.product_id) FROM Order_Items oi " +
        "  WHERE oi.order_id = r.order_id" +
        ")")
    List<Integer> findFullyReviewedOrderIds(@Param("userId") Integer userId);

    interface ReviewSummaryProjection {
        Integer getReviewId();
        Integer getProductId();
        Integer getUserId();
        String getUserName();
        Integer getRating();
        String getComment();
        String getImages();
        Boolean getIsVerified();
        LocalDateTime getCreatedAt();
    }
}
