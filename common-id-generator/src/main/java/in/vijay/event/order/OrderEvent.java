package in.vijay.event.order;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderEvent {

    private String orderId;
    private LocalDateTime eventDateTime = LocalDateTime.now();
}

