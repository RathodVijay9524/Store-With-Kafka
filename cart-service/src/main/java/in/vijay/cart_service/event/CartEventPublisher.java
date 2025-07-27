package in.vijay.cart_service.event;

import in.vijay.event.cart.CartClearedEvent;
import in.vijay.event.cart.CartCreatedEvent;
import in.vijay.event.cart.CartItemAddedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCartCreated(CartCreatedEvent event) {
        kafkaTemplate.send("cart-created-topic", event);
    }

    public void publishCartItemAdded(CartItemAddedEvent event) {
        kafkaTemplate.send("cart-item-added-topic", event);
    }

    public void publishCartCleared(CartClearedEvent event) {
        kafkaTemplate.send("cart-cleared-topic", event);
    }
}

