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
    private String orderId;
    private String shipmentId;
    private String completedDate;
    private String productId;

}

