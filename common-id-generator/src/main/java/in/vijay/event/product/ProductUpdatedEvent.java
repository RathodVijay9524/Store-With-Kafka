package in.vijay.event.product;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent {
    private String productId;
    private String name;
    private String skuCode;
    private String brand;
    private BigDecimal price;
    private Long categoryId;
}

