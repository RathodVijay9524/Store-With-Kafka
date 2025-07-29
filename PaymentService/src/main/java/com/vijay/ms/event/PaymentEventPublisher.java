package com.vijay.ms.event;


import in.vijay.event.order.OrderCancelledEvent;
import in.vijay.event.payments.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
        kafkaTemplate.send("payment.completed", event);
    }

    public void publishOrderCancelledEvent(OrderCancelledEvent event) {

        kafkaTemplate.send("order.cancelled", event);
    }

}
