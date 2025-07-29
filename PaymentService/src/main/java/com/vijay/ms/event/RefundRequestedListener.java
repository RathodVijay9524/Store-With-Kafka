package com.vijay.ms.event;

import com.vijay.ms.entity.Payment;
import com.vijay.ms.repository.PaymentRepository;
import in.vijay.event.payments.RefundRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RefundRequestedListener {

    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "refund.requested", groupId = "payment-group")
    public void onRefundRequested(RefundRequestedEvent event) {
        log.info("💸 Received RefundRequestedEvent: {}", event);

        Optional<Payment> optionalPayment = paymentRepository.findById(event.getPaymentId());

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setStatus("REFUNDED"); // or "CANCELLED"
            paymentRepository.save(payment);

            log.info("✅ Payment [{}] marked as REFUNDED", payment.getPaymentId());
        } else {
            log.warn("❌ Payment [{}] not found for refund", event.getPaymentId());
        }
    }
}
