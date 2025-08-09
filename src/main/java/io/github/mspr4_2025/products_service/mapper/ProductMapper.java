package io.github.mspr4_2025.products_service.mapper;

import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.model.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    List<ProductDto> fromEntities(Collection<ProductEntity> entities);

    @Mapping(target = "stockUid", source = "stock.uid")
    @Mapping(target = "name", source = "stock.productName")
    ProductDto fromEntity(ProductEntity entity);

    ProductEntity fromCreateDto(ProductCreateDto createDto);
}
