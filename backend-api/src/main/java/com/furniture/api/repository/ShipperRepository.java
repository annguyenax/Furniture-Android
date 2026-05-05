package com.furniture.api.repository;

import com.furniture.api.model.Shipper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper, Integer> {

    Optional<Shipper> findByUserId(Integer userId);

    Optional<Shipper> findByPhone(String phone);

    Optional<Shipper> findByLicensePlate(String licensePlate);

    Page<Shipper> findByStatus(Shipper.ShipperStatus status, Pageable pageable);

    boolean existsByUserId(Integer userId);

    boolean existsByPhone(String phone);

    boolean existsByLicensePlate(String licensePlate);
}
