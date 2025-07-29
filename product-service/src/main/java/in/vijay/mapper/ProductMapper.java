package in.vijay.mapper;


import in.vijay.dto.product.ProductRequest;
import in.vijay.dto.product.ProductResponse;
import in.vijay.beans.Product;
import in.vijay.event.product.ProductCreatedEvent;
import in.vijay.event.product.ProductDeletedEvent;
import in.vijay.event.product.ProductUpdatedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper implements GenericMapperInterface<Product, ProductRequest, ProductResponse> {

    @Override
    public Product toEntity(ProductRequest dto) {
        return Product.builder()
                .skuCode(dto.getSkuCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .brand(dto.getBrand())
                .imageUrl(dto.getImageUrl())
                .price(dto.getPrice())
                .unit(dto.getUnit())
                .active(dto.isActive())
                .categoryId(dto.getCategoryId())
                .build();
    }

    @Override
    public ProductResponse toResponseDto(Product entity) {
        return ProductResponse.builder()
                .id(entity.getId())
                .skuCode(entity.getSkuCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .brand(entity.getBrand())
                .imageUrl(entity.getImageUrl())
                .price(entity.getPrice())
                .unit(entity.getUnit())
                .active(entity.isActive())
                .categoryId(entity.getCategoryId())
                .build();
    }

    @Override
    public List<ProductResponse> toResponseDtoList(List<Product> entityList) {
        return entityList.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ProductCreatedEvent toCreatedEvent(Product product) {
        return ProductCreatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .skuCode(product.getSkuCode())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .build();
    }

    public ProductUpdatedEvent toUpdatedEvent(Product product) {
        return ProductUpdatedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .skuCode(product.getSkuCode())
                .price(product.getPrice())
                .categoryId(product.getCategoryId())
                .build();
    }

    public ProductDeletedEvent toDeletedEvent(Product product) {
        return ProductDeletedEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .build();
    }

}



/*

public ProductRequest toDto() { // inside Entity
    return new ProductRequest(this.name, this.description, this.price, this.quantity, this.inStock);
}

@Component
public class ProductMapper {
    public ProductRequest toRequest(Product product) {
        return new ProductRequest(
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity(),
            product.getInStock()
        );
    }
}


@Component
public class ProductMapper {
    public ProductRequest toDto(Product entity) {
        return ProductRequest.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .quantity(entity.getQuantity())
                .inStock(entity.getInStock())
                .build();
    }

    public Product toEntity(ProductRequest dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .quantity(dto.getQuantity())
                .inStock(dto.getInStock())
                .build();
    }
}*/
