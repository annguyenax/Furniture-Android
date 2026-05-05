package com.furniture.api.repository;

import com.furniture.api.model.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Integer> {

    Optional<Shipment> findBySubOrderId(Integer subOrderId);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    List<Shipment> findByShipperId(Integer shipperId);

    Page<Shipment> findByShipperIdAndStatus(Integer shipperId, Shipment.ShipmentStatus status, Pageable pageable);

    Page<Shipment> findByStatus(Shipment.ShipmentStatus status, Pageable pageable);

    Long countByShipperId(Integer shipperId);

    Long countByShipperIdAndStatus(Integer shipperId, Shipment.ShipmentStatus status);
}
