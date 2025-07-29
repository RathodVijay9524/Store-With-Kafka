package com.vijay.ms.event;

import com.vijay.ms.util.PrefixGenerator;
import in.vijay.event.enventory.InventoryReservedEvent;
import in.vijay.event.order.OrderCancelledEvent;
import in.vijay.event.payments.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.vijay.ms.entity.Payment;
import com.vijay.ms.repository.PaymentRepository;


import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryReservedListener {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher eventPublisher;

    @KafkaListener(topics = "inventory.reserved", groupId = "payment-group")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("Received InventoryReservedEvent: {}", event);

        try {
            // Simulate a payment process
            Payment payment = Payment.builder()
                    .paymentId(PrefixGenerator.generateDateBased("PAY"))
                    .orderId(event.getOrderId())
                    .amount(100.0) // In a real app, lookup order/payment info
                    .method("UPI")
                    .status("DONE")
                    .productId(event.getProductId())
                    .quantity(event.getReservedQuantity())
                    .orderId(event.getOrderId())
                    .paymentDate(LocalDate.now().toString())
                    .build();

            paymentRepository.save(payment);

            eventPublisher.publishPaymentCompletedEvent(PaymentCompletedEvent.builder()
                    .orderId(event.getOrderId())
                    .paymentId(payment.getPaymentId())
                    .paidAmount(payment.getAmount())
                    .productId(payment.getProductId())     // ✅ set it here
                    .quantity(event.getReservedQuantity())
                    .paymentMethod(payment.getMethod())
                    .paymentDate(payment.getPaymentDate())
                    .build());
            log.info("Payment completed for order {}", event.getOrderId());

        } catch (Exception e) {
            log.error("Payment failed for order {}", event.getOrderId(), e);

            eventPublisher.publishOrderCancelledEvent(OrderCancelledEvent.builder()
                    .orderId(event.getOrderId())
                    .reason("Payment failed")
                    .cancelledDate(LocalDate.now().toString())
                    .build());
        }
    }
}

