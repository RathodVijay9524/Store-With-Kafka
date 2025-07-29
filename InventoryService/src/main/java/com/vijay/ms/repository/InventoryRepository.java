package com.vijay.ms.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.vijay.ms.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
