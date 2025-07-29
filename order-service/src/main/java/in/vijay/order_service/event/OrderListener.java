package in.vijay.order_service.event;


import in.vijay.dto.order.OrderStatus;
import in.vijay.event.order.OrderCancelledEvent;
import in.vijay.event.order.OrderCompletedEvent;
import in.vijay.order_service.beans.Order;
import in.vijay.order_service.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
@Slf4j
public class OrderListener {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "order.cancelled", groupId = "order-group")
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("❌ Received OrderCancelledEvent: {}", event);

        Optional<Order> optionalOrder = orderRepository.findById(event.getOrderId());

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(OrderStatus.CANCELLED_PENDING);
            orderRepository.save(order);
            log.info("🚫 Order [{}] marked as CANCELLED", order.getId());
        } else {
            log.warn("⚠️ Order with ID [{}] not found for cancellation.", event.getOrderId());
        }
    }

    @KafkaListener(topics = "order.completed", groupId = "order-group")
    public void onOrderCompleted(OrderCompletedEvent event) {
        log.info("📦 Received OrderCompletedEvent: {}", event);

        // Update order status to COMPLETED
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.SHIPPED);
            orderRepository.save(order);
            log.info("✅ Order {} marked as COMPLETED", event.getOrderId());
        });
    }
}

