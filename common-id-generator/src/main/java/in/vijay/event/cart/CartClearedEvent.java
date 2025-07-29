package in.vijay.event.cart;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartClearedEvent {
    private String cartId;
    private String userId;
    private LocalDateTime clearedAt;
}

