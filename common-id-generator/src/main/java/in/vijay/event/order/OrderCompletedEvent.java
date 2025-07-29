package in.vijay.event.order;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderCompletedEvent extends OrderEvent {
    private LocalDateTime completedAt;
}

