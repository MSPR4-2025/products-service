package io.github.mspr4_2025.products_service.mapper;

import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.model.ProductDto;
import io.github.mspr4_2025.products_service.model.ProductUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductDto> fromEntities(Collection<ProductEntity> entities);

    ProductDto fromEntity(ProductEntity entity);

    ProductEntity fromCreateDto(ProductCreateDto createDto);

    void updateEntityFromDto(ProductUpdateDto dto, @MappingTarget ProductEntity entity);
}
