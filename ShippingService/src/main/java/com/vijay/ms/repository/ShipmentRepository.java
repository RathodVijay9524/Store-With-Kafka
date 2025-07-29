package com.vijay.ms.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vijay.ms.entity.Shipment;

public interface ShipmentRepository extends JpaRepository<Shipment, String> {
	Optional<Shipment> findByOrderId(String orderId);
}

