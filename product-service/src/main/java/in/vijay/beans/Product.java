package in.vijay.beans;

import in.vijay.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity<String> {

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
