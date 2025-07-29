package com.vijay.ms.event;

import com.vijay.ms.util.PrefixGenerator;
import in.vijay.event.enventory.InventoryRollbackEvent;
import in.vijay.event.order.OrderCancelledEvent;
import in.vijay.event.order.OrderCompletedEvent;
import in.vijay.event.order.OrderShippedEvent;
import in.vijay.event.payments.PaymentCompletedEvent;
import in.vijay.event.payments.RefundRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.vijay.ms.entity.Shipment;
import com.vijay.ms.repository.ShipmentRepository;


import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCompletedListener {

    private final ShipmentRepository shipmentRepository;
    private final ShippingEventPublisher eventPublisher;

    @KafkaListener(topics = "payment.completed", groupId = "shipping-group")
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("✅ Received PaymentCompletedEvent: {}", event);

        try {

            // ✅ Simulate shipment logic (replace with real logic)
            Shipment shipment = Shipment.builder()
                    .shipmentId(PrefixGenerator.generateDateBased("SIP")) // or use your ID generator
                    .orderId(event.getOrderId())
                    .productId(event.getProductId())
                    .shippingAddress("Default Shipping Address") // must exist in event
                    .shippingDate(LocalDate.now().toString())
                    .status("SHIPPED")
                    .build();
            shipmentRepository.save(shipment);


            // ✅ Publish OrderCompletedEvent
            eventPublisher.publishOrderCompletedEvent(
                    OrderCompletedEvent.builder()
                            .orderId(event.getOrderId())
                            .shipmentId(shipment.getShipmentId())
                            .productId(shipment.getProductId())
                            .completedDate(LocalDate.now().toString())
                            .build()
            );
            log.info("🎉 OrderCompletedEvent published for order {}", event.getOrderId());

            eventPublisher.publishOrderShippedEvent(
                    OrderShippedEvent.builder()
                            .shippingId(shipment.getShipmentId())
                            .address(shipment.getShippingAddress())
                            .shippedDate(shipment.getShippingDate())
                            .orderId(shipment.getOrderId())
                            .build()
            );



        } catch (Exception ex) {
            log.error("❌ Shipping failed for order {}", event.getOrderId(), ex);

            // ⛔ Shipping failed — trigger refund
            eventPublisher.publishRefundRequestedEvent(
                    RefundRequestedEvent.builder()
                            .orderId(event.getOrderId())
                            .paymentId(event.getPaymentId()) // must be in PaymentCompletedEvent
                            .reason("Shipping failed")
                            .requestedDate(LocalDate.now().toString())
                            .build()
            );

            log.warn("🔁 RefundRequestedEvent published for failed shipment of order {}", event.getOrderId());

            // Optional: also cancel the order
            eventPublisher.publishOrderCancelledEvent(
                    OrderCancelledEvent.builder()
                            .orderId(event.getOrderId())
                            .reason("Shipping failed")
                            .cancelledDate(LocalDate.now().toString())
                            .build()
            );

            // ♻️ 2. Rollback inventory
            eventPublisher.publishInventoryRollbackEvent(
                    InventoryRollbackEvent.builder()
                            .orderId(event.getOrderId())
                            .productId(event.getProductId())
                            .quantity(event.getQuantity())
                            .reason("Shipping failed")
                            .rollbackDate(LocalDate.now().toString())
                            .build()
            );
        }
    }
}

