package com.furniture.api.repository;

import com.furniture.api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findBySubOrderId(Integer subOrderId);

    void deleteBySubOrderId(Integer subOrderId);

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT p.product_name, SUM(oi.total) as revenue " +
                "FROM Order_Items oi " +
                "JOIN Sub_Orders so ON oi.sub_order_id = so.sub_order_id " +
                "JOIN Orders o ON so.order_id = o.order_id " +
                "JOIN Products p ON oi.product_id = p.product_id " +
                "WHERE o.status = 'DELIVERED' " +
                "GROUP BY oi.product_id, p.product_name " +
                "ORDER BY revenue DESC LIMIT 10",
        nativeQuery = true)
    java.util.List<Object[]> getTopProductsByRevenue();

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT c.category_name, SUM(oi.total) as revenue " +
                "FROM Order_Items oi " +
                "JOIN Sub_Orders so ON oi.sub_order_id = so.sub_order_id " +
                "JOIN Orders o ON so.order_id = o.order_id " +
                "JOIN Products p ON oi.product_id = p.product_id " +
                "JOIN Categories c ON p.category_id = c.category_id " +
                "WHERE o.status = 'DELIVERED' " +
                "GROUP BY c.category_id, c.category_name ORDER BY revenue DESC",
        nativeQuery = true)
    java.util.List<Object[]> getRevenueByCategory();
}
