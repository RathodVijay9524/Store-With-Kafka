package in.vijay.mapper;

import in.vijay.beans.Product;
import in.vijay.dto.product.ProductRequest;
import in.vijay.dto.product.ProductResponse;
import in.vijay.event.product.ProductCreatedEvent;
import in.vijay.event.product.ProductDeletedEvent;
import in.vijay.event.product.ProductUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMappers extends GenericMapperInterface<Product, ProductRequest, ProductResponse> {

    @Override
    Product toEntity(ProductRequest dto);

    @Override
    ProductResponse toResponseDto(Product entity);

    @Mapping(source = "id", target = "productId")
    ProductCreatedEvent toCreatedEvent(Product product);

    @Mapping(source = "id", target = "productId")
    ProductUpdatedEvent toUpdatedEvent(Product product);

    @Mapping(source = "id", target = "productId")
    ProductDeletedEvent toDeletedEvent(Product product);
}

