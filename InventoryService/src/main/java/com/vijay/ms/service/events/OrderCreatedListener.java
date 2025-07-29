package com.vijay.ms.service.events;


import in.vijay.event.enventory.InventoryReservedEvent;
import in.vijay.event.order.OrderCancelledEvent;
import in.vijay.event.order.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.vijay.ms.entity.Inventory;
import com.vijay.ms.repository.InventoryRepository;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedListener {

    private final InventoryRepository inventoryRepository;
    private final InventoryEventPublisher eventPublisher;

    @KafkaListener(topics = "order.created", groupId = "inventory-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Received OrderCreatedEvent: {}", event);

            Optional<Inventory> optional = inventoryRepository.findById(event.getProductId());

            if (optional.isPresent() && optional.get().getAvailableQuantity() >= event.getQuantity()) {
                Inventory inventory = optional.get();
                inventory.setAvailableQuantity(inventory.getAvailableQuantity() - event.getQuantity());
                inventoryRepository.save(inventory);

                eventPublisher.publishInventoryReservedEvent(InventoryReservedEvent.builder()
                        .orderId(event.getOrderId())
                        .productId(event.getProductId())
                        .reservedQuantity(event.getQuantity())
                        .reservedDate(LocalDate.now().toString())
                        .build());

                log.info("Inventory reserved for order {}", event.getOrderId());

            } else {
                eventPublisher.publishOrderCancelledEvent(OrderCancelledEvent.builder()
                        .orderId(event.getOrderId())
                        .reason("Insufficient inventory")
                        .cancelledDate(LocalDate.now().toString())
                        .build());

                log.warn("Inventory not available, cancelling order {}", event.getOrderId());
            }
        } catch (Exception e) {
            log.error("Error handling OrderCreatedEvent: {}", event, e);
            throw e; // re-throw to allow retry/DLQ
        }
    }

}

