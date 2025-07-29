package in.vijay.event.order;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderShippedEvent extends OrderEvent {
    private String trackingId;
    private LocalDateTime shippedAt;
}

