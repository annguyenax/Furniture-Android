package com.furniture.api.repository;

import com.furniture.api.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    List<CartItem> findByCartId(Integer cartId);

    Optional<CartItem> findByCartIdAndProductIdAndProductVariantId(
        Integer cartId, Integer productId, Integer productVariantId);

    Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);

    void deleteByCartId(Integer cartId);

    Long countByCartId(Integer cartId);
}
