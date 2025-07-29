package in.vijay.event;

import in.vijay.beans.Product;
import in.vijay.event.product.ProductCreatedEvent;
import in.vijay.event.product.ProductDeletedEvent;
import in.vijay.event.product.ProductUpdatedEvent;
import in.vijay.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductMapper productMapper;


    public void publishProductCreated(Product product) {
        ProductCreatedEvent event = productMapper.toCreatedEvent(product);
        kafkaTemplate.send("product-created-topic", event);
    }

    public void publishProductUpdated(Product product) {
        ProductUpdatedEvent event = productMapper.toUpdatedEvent(product);
        kafkaTemplate.send("product-updated-topic", event);
    }

    public void publishProductDeleted(Product product) {
        ProductDeletedEvent event = productMapper.toDeletedEvent(product);
        kafkaTemplate.send("product-deleted-topic", event);
    }
}

