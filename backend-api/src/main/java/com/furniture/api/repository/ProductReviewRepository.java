package com.furniture.api.repository;

import com.furniture.api.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    Page<ProductReview> findByProductId(Integer productId, Pageable pageable);

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
        "  INNER JOIN Sub_Orders so ON oi.sub_order_id = so.sub_order_id " +
        "  WHERE so.order_id = r.order_id" +
        ")")
    List<Integer> findFullyReviewedOrderIds(@Param("userId") Integer userId);
}
