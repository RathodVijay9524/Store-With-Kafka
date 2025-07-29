package com.vijay.ms.event;


import com.vijay.ms.event.order.OrderShippedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.vijay.ms.service.NotificationService;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderShippedListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order.shipped", groupId = "notification-group")
    public void onOrderShipped(OrderShippedEvent event) {
        log.info("✅ Received OrderShippedEvent: {}", event);
        notificationService.notifyShipped(event.getOrderId(), event.getShippingId(), event.getAddress());
    }
}
