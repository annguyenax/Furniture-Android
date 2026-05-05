package com.furniture.api.repository;

import com.furniture.api.model.ShopReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopReviewRepository extends JpaRepository<ShopReview, Integer> {

    Page<ShopReview> findByShopId(Integer shopId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM ShopReview r WHERE r.shopId = :shopId")
    Double getAverageRatingByShopId(@Param("shopId") Integer shopId);

    Long countByShopId(Integer shopId);

    boolean existsByShopIdAndUserId(Integer shopId, Integer userId);
}
