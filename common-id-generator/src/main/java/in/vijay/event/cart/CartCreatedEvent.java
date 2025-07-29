package in.vijay.event.cart;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartCreatedEvent {
    private String cartId;
    private String userId;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
}

