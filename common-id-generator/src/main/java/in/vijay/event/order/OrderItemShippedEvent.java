package in.vijay.event.order;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemShippedEvent {
    private String itemId;
    private String orderId;
    private String productId;
    private LocalDateTime shippedAt;
}
