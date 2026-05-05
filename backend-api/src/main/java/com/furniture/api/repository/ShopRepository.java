package com.furniture.api.repository;

import com.furniture.api.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    Optional<Shop> findByOwnerId(Integer ownerId);
    Optional<Shop> findByShopName(String shopName);
    boolean existsByOwnerId(Integer ownerId);
    boolean existsByShopName(String shopName);
}
