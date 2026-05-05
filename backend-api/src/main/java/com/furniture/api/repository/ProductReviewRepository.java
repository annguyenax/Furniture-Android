package com.furniture.api.repository;

import com.furniture.api.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {

    Page<ProductReview> findByProductId(Integer productId, Pageable pageable);

    List<ProductReview> findByUserId(Integer userId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") Integer productId);

    Long countByProductId(Integer productId);

    boolean existsByProductIdAndUserId(Integer productId, Integer userId);
}
