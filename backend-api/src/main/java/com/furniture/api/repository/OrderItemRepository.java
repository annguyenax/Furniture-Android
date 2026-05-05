package com.furniture.api.repository;

import com.furniture.api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findBySubOrderId(Integer subOrderId);

    void deleteBySubOrderId(Integer subOrderId);
}
