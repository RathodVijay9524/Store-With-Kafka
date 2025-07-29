package in.vijay.category_service.event;


import in.vijay.event.category.CategoryCreatedEvent;
import in.vijay.event.category.CategoryDeletedEvent;
import in.vijay.event.category.CategoryUpdatedEvent;
import in.vijay.category_service.beans.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper modelMapper;

    public void publishCategoryCreatedEvent(Category category) {
        CategoryCreatedEvent event = modelMapper.map(category, CategoryCreatedEvent.class);
        kafkaTemplate.send("category-created", event);
    }

    public void publishCategoryUpdatedEvent(Category category) {
        CategoryUpdatedEvent event = modelMapper.map(category, CategoryUpdatedEvent.class);
        kafkaTemplate.send("category-updated", event);
    }

    public void publishCategoryDeletedEvent(Category category) {
        CategoryDeletedEvent event = modelMapper.map(category, CategoryDeletedEvent.class);
        kafkaTemplate.send("category-deleted", event);
    }
}


