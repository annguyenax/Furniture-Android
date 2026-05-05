package com.furniture.api.repository;

import com.furniture.api.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    Optional<Coupon> findByCode(String code);

    List<Coupon> findByShopId(Integer shopId);

    List<Coupon> findByShopIdIsNull();

    @Query("SELECT c FROM Coupon c WHERE c.status = 'ACTIVE' AND c.startDate <= :now AND c.endDate >= :now")
    List<Coupon> findActiveCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.shopId = :shopId AND c.status = 'ACTIVE' AND c.startDate <= :now AND c.endDate >= :now")
    List<Coupon> findActiveShopCoupons(@Param("shopId") Integer shopId, @Param("now") LocalDateTime now);

    boolean existsByCode(String code);
}
