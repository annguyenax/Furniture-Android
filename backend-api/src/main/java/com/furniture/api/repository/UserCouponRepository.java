package com.furniture.api.repository;

import com.furniture.api.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {

    List<UserCoupon> findByUserId(Integer userId);

    List<UserCoupon> findByUserIdAndUsedAtIsNull(Integer userId);

    Optional<UserCoupon> findByUserIdAndCouponId(Integer userId, Integer couponId);

    boolean existsByUserIdAndCouponId(Integer userId, Integer couponId);
}
