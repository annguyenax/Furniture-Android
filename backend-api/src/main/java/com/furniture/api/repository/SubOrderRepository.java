package com.furniture.api.repository;

import com.furniture.api.model.SubOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SubOrderRepository extends JpaRepository<SubOrder, Integer> {

    List<SubOrder> findByOrderId(Integer orderId);

    Page<SubOrder> findByShopId(Integer shopId, Pageable pageable);

    Page<SubOrder> findByShopIdAndStatus(Integer shopId, SubOrder.SubOrderStatus status, Pageable pageable);

    List<SubOrder> findByStatus(SubOrder.SubOrderStatus status);

    Long countByShopId(Integer shopId);

    Long countByShopIdAndStatus(Integer shopId, SubOrder.SubOrderStatus status);

    @Query("SELECT SUM(s.totalPrice) FROM SubOrder s WHERE s.shopId = :shopId AND s.status = 'DELIVERED'")
    BigDecimal getTotalRevenueByShopId(@Param("shopId") Integer shopId);

    @Query("SELECT MONTH(s.createdAt) as month, SUM(s.totalPrice) as revenue FROM SubOrder s " +
           "WHERE s.shopId = :shopId AND YEAR(s.createdAt) = :year AND s.status = 'DELIVERED' " +
           "GROUP BY MONTH(s.createdAt)")
    List<Object[]> getMonthlyRevenueByShopIdAndYear(@Param("shopId") Integer shopId, @Param("year") Integer year);
}
