package in.vijay.event.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemEvent {
    private String itemId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
