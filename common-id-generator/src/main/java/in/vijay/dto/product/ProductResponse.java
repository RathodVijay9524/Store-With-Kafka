package in.vijay.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.NoArgsConstructor;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private String id;
    private String skuCode;
    private String name;
    private String description;
    private String brand;
    private String imageUrl;
    private BigDecimal price;
    private String unit;
    private boolean active;
    private Long categoryId;
}

