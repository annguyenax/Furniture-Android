package com.furniture.api.repository;

import com.furniture.api.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    List<Wishlist> findByUserId(Integer userId);

    Optional<Wishlist> findByUserIdAndProductId(Integer userId, Integer productId);

    void deleteByUserIdAndProductId(Integer userId, Integer productId);

    void deleteByUserId(Integer userId);

    boolean existsByUserIdAndProductId(Integer userId, Integer productId);

    Long countByUserId(Integer userId);
}
