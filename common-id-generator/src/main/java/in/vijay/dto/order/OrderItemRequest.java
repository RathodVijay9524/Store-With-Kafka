package in.vijay.dto.order;



import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price; // price per unit
}

