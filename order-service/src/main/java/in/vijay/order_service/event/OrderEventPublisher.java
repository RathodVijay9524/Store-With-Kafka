package in.vijay.order_service.event;

import in.vijay.event.enventory.InventoryReservedEvent;
import in.vijay.event.enventory.InventoryRollbackEvent;
import in.vijay.event.order.*;
import in.vijay.order_service.beans.Order;
import in.vijay.order_service.beans.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper modelMapper;

    public void publishOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = modelMapper.map(order, OrderCreatedEvent.class);
        List<OrderItemEvent> itemEvents = order.getItems().stream()
                .map(item -> modelMapper.map(item, OrderItemEvent.class))
                .collect(Collectors.toList());
        event.setItems(itemEvents);
        kafkaTemplate.send("order.created", event);
    }

    public void publishOrderCancelledEvent(Order order, String reason) {
        OrderCancelledEvent event = modelMapper.map(order, OrderCancelledEvent.class);
        event.setReason(reason);
        kafkaTemplate.send("order.cancelled", event);
    }

    public void publishOrderShippedEvent(Order order, String trackingId) {
        OrderShippedEvent event = modelMapper.map(order, OrderShippedEvent.class);
        event.setTrackingId(trackingId);
        //event.setShippedAt(order.getUpdatedAt()); // Or LocalDateTime.now()
        kafkaTemplate.send("order.shipped", event);
    }

    public void publishOrderCompletedEvent(Order order) {
        OrderCompletedEvent event = modelMapper.map(order, OrderCompletedEvent.class);
        //event.setCompletedAt(order.getUpdatedAt()); // Or LocalDateTime.now()
        kafkaTemplate.send("order.completed", event);
    }

    public void publishOrderItemShippedEvent(OrderItem orderItem) {
        OrderItemShippedEvent event = modelMapper.map(orderItem, OrderItemShippedEvent.class);
        //event.setShippedAt(orderItem.getUpdatedAt()); // Or LocalDateTime.now()
        event.setOrderId(orderItem.getOrder().getId());
        kafkaTemplate.send("order.item.shipped", event);
    }

    public void publishOrderItemCancelledEvent(OrderItem orderItem, String reason) {
        OrderItemCancelledEvent event = modelMapper.map(orderItem, OrderItemCancelledEvent.class);
        event.setOrderId(orderItem.getOrder().getId());
        event.setReason(reason);
        kafkaTemplate.send("order.item.cancelled", event);
    }
    public void publishInventoryReservedEvent(InventoryReservedEvent event) {
        kafkaTemplate.send("inventory.reserved", event);
    }

    public void publishInventoryRollbackEvent(InventoryRollbackEvent event) {
        kafkaTemplate.send("inventory.rollback", event);
    }
}
