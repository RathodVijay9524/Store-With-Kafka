package com.vijay.ms.event;



import in.vijay.event.enventory.InventoryRollbackEvent;
import in.vijay.event.order.OrderCancelledEvent;
import in.vijay.event.order.OrderCompletedEvent;
import in.vijay.event.order.OrderShippedEvent;
import in.vijay.event.payments.RefundRequestedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShippingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ShippingEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
		super();
		this.kafkaTemplate = kafkaTemplate;
	}

	public void publishOrderShippedEvent(OrderShippedEvent event) {

        kafkaTemplate.send("order.shipped", event);
    }

    public void publishOrderCancelledEvent(OrderCancelledEvent event) {

        kafkaTemplate.send("order.cancelled", event);
    }
    public void publishRefundRequestedEvent(RefundRequestedEvent event) {
        kafkaTemplate.send("refund.requested", event);
    }
    public void publishInventoryRollbackEvent(InventoryRollbackEvent event) {
        kafkaTemplate.send("inventory.rollback", event);
    }
    public void publishOrderCompletedEvent(OrderCompletedEvent event) {
        kafkaTemplate.send("order.completed", event);
    }
    public void publishShippedEvent(OrderCompletedEvent event) {

        kafkaTemplate.send("shipping.completed", event);
    }

}

