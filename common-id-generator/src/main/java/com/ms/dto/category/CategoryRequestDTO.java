package com.ms.dto.category;

import lombok.Data;

@Data
public class CategoryRequestDTO {


    private String name;

    private String description;

    private String imageUrl;

    private boolean active;
}
