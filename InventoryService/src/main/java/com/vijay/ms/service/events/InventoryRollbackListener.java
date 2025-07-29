package com.vijay.ms.service.events;

import com.vijay.ms.entity.Inventory;
import com.vijay.ms.repository.InventoryRepository;
import in.vijay.event.enventory.InventoryRollbackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class  InventoryRollbackListener {

    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "inventory.rollback", groupId = "inventory-group")
    public void handleInventoryRollback(InventoryRollbackEvent event) {
        log.info("📩 Received InventoryRollbackEvent: {}", event);
        Optional<Inventory> optional = inventoryRepository.findById(event.getProductId());

        if (optional.isPresent() && optional.get().getAvailableQuantity() >= event.getQuantity()) {
            Inventory inventory = optional.get();
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - event.getQuantity());
            inventoryRepository.save(inventory);
            log.info("✅ Inventory restored for product [{}] by [{}]", event.getProductId(), event.getQuantity());
            log.info("✅ Rolled back inventory for product {} by quantity {}", event.getProductId(), event.getQuantity());
        } else {
            log.warn("❌ Inventory not found for product [{}]", event.getProductId());

        }
    }

}

