package in.vijay.dto.order;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private String id;
    private String userId;
    private BigDecimal totalPrice;
    private List<CartItemResponse> items;
}


