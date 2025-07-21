package in.vijay.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserCreatedEvent(UserCreatedEvent event) {

        kafkaTemplate.send("user-created", event);
    }

    public void publishUserDeletedEvent(UserDeletedEvent event) {

        kafkaTemplate.send("user-deleted", event);
    }

    public void publishUserUpdatedEvent(UserUpdatedEvent event) {

        kafkaTemplate.send("user-updated", event);
    }
}
