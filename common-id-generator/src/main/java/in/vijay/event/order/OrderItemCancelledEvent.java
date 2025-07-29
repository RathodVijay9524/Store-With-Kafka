package in.vijay.event.order;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemCancelledEvent {
    private String itemId;
    private String orderId;
    private String reason;
}

