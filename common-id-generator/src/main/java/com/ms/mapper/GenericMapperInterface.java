package com.ms.mapper;

import java.util.List;

public interface GenericMapperInterface<T, Req, Res> {
    T toEntity(Req dto);
    Res toResponseDto(T entity);
    List<Res> toResponseDtoList(List<T> entityList);
}



/*public interface GenericMapperInterface<T, Req, Res> {

    T toEntity(Req dto);

    Res toResponseDto(T entity);

    default List<Res> toResponseDtoList(List<T> entities) {
        return entities.stream()
                .map(this::toResponseDto)
                .toList(); // Java 16+ (or use collect(Collectors.toList()) for earlier)
    }
}*/
