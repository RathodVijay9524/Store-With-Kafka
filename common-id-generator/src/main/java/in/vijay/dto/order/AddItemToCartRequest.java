package in.vijay.dto.order;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemToCartRequest {
    private String productId;
    private Integer quantity;
}
