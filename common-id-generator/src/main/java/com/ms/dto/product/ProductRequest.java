package com.ms.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Boolean inStock;
    private String categoryId;

}
