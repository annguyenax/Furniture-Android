package com.furniture.api.repository;

import com.furniture.api.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrderId(Integer orderId);

    void deleteByOrderId(Integer orderId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
        "FROM OrderItem oi " +
        "WHERE oi.orderId = :orderId AND oi.productId = :productId")
    boolean existsByOrderIdAndProductId(
            @org.springframework.data.repository.query.Param("orderId") Integer orderId,
            @org.springframework.data.repository.query.Param("productId") Integer productId);

    boolean existsByVariantId(Integer variantId);

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT COALESCE(SUM(oi.quantity), 0) " +
                "FROM Order_Items oi " +
                "JOIN Orders o ON oi.order_id = o.order_id " +
                "WHERE oi.product_id = :productId AND o.status = 'DELIVERED'",
        nativeQuery = true)
    Long sumDeliveredQuantityByProductId(
            @org.springframework.data.repository.query.Param("productId") Integer productId);

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT p.product_name, SUM(oi.total) as revenue " +
                "FROM Order_Items oi " +
                "JOIN Orders o ON oi.order_id = o.order_id " +
                "JOIN Products p ON oi.product_id = p.product_id " +
                "WHERE o.status = 'DELIVERED' " +
                "GROUP BY oi.product_id, p.product_name " +
                "ORDER BY revenue DESC LIMIT 10",
        nativeQuery = true)
    java.util.List<Object[]> getTopProductsByRevenue();

    @org.springframework.data.jpa.repository.Query(
        value = "SELECT c.category_name, SUM(oi.total) as revenue " +
                "FROM Order_Items oi " +
                "JOIN Orders o ON oi.order_id = o.order_id " +
                "JOIN Products p ON oi.product_id = p.product_id " +
                "JOIN Categories c ON p.category_id = c.category_id " +
                "WHERE o.status = 'DELIVERED' " +
                "GROUP BY c.category_id, c.category_name ORDER BY revenue DESC",
        nativeQuery = true)
    java.util.List<Object[]> getRevenueByCategory();
}
