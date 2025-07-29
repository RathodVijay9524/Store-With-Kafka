package com.vijay.ms.event;



import com.vijay.ms.event.order.OrderCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.vijay.ms.service.NotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order.cancelled", groupId = "notification-group")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("⚠️ Received OrderCancelledEvent: {}", event);
        notificationService.notifyCancelled(event.getOrderId(), event.getReason());
    }
}

