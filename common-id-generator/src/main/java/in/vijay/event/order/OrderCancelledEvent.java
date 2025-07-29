package in.vijay.event.order;


import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderCancelledEvent extends OrderEvent {
    private String reason;
}

