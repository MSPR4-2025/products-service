package io.github.mspr4_2025.products_service.mapper;

import io.github.mspr4_2025.products_service.entity.StockEntity;
import io.github.mspr4_2025.products_service.model.StockDto;
import org.mapstruct.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMapper {

    List<StockDto> fromEntities(Collection<StockEntity> entities);
    StockDto fromEntity(StockEntity entity);
}