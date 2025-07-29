package in.vijay.event.cart;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemAddedEvent {
    private String cartItemId;
    private String productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private String cartId;
}
