package com.vijay.ms.service.events;

import in.vijay.event.enventory.InventoryReservedEvent;
import in.vijay.event.order.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishInventoryReservedEvent(InventoryReservedEvent event) {
        kafkaTemplate.send("inventory.reserved", event);
    }

    public void publishOrderCancelledEvent(OrderCancelledEvent event) {

        kafkaTemplate.send("order.cancelled", event);
    }
}

