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

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT DATE(created_at) as period, SUM(total_price) as revenue " +
                "FROM Orders WHERE status = 'DELIVERED' AND created_at >= :since " +
                "GROUP BY DATE(created_at) ORDER BY period",
        nativeQuery = true)
    java.util.List<Object[]> getDailyRevenue(@org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since);

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT DATE_FORMAT(created_at, '%Y-%m') as period, SUM(total_price) as revenue " +
                "FROM Orders WHERE status = 'DELIVERED' AND created_at >= :since " +
                "GROUP BY DATE_FORMAT(created_at, '%Y-%m') ORDER BY period",
        nativeQuery = true)
    java.util.List<Object[]> getMonthlyRevenue(@org.springframework.data.repository.query.Param("since") java.time.LocalDateTime since);

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT YEAR(created_at) as period, SUM(total_price) as revenue " +
                "FROM Orders WHERE status = 'DELIVERED' " +
                "GROUP BY YEAR(created_at) ORDER BY period",
        nativeQuery = true)
    java.util.List<Object[]> getYearlyRevenue();
}
