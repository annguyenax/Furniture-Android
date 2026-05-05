package com.furniture.api.repository;

import com.furniture.api.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findByUserId(Integer userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Integer userId, Order.OrderStatus status, Pageable pageable);

    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);

    Page<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus, Pageable pageable);

    Long countByUserId(Integer userId);

    Long countByStatus(Order.OrderStatus status);
}
