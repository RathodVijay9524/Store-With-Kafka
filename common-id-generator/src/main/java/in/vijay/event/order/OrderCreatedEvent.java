package in.vijay.event.order;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderCreatedEvent extends OrderEvent {
    private String userId;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemEvent> items;
}

