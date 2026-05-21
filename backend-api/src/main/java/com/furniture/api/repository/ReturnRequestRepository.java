package com.furniture.api.repository;

import com.furniture.api.model.ReturnRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Integer> {

    Page<ReturnRequest> findByUserId(Integer userId, Pageable pageable);

    Page<ReturnRequest> findByStatus(ReturnRequest.ReturnStatus status, Pageable pageable);

    boolean existsByOrderIdAndOrderItemIdAndUserIdAndStatusIn(
            Integer orderId,
            Integer orderItemId,
            Integer userId,
            Collection<ReturnRequest.ReturnStatus> statuses);

    Optional<ReturnRequest> findTopByOrderIdAndUserIdOrderByCreatedAtDesc(Integer orderId, Integer userId);

    @Query("SELECT DISTINCT r.orderId FROM ReturnRequest r WHERE r.userId = :userId AND r.status IN :statuses")
    List<Integer> findOrderIdsByUserIdAndStatusIn(
            @Param("userId") Integer userId,
            @Param("statuses") Collection<ReturnRequest.ReturnStatus> statuses);

    boolean existsByOrderIdAndStatus(Integer orderId, ReturnRequest.ReturnStatus status);
}
