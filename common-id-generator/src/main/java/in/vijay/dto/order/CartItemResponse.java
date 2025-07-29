package in.vijay.dto.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private String id;
    private String productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
}
