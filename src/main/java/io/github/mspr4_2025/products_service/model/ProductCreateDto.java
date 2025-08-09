package io.github.mspr4_2025.products_service.model;

import java.util.UUID;
import io.github.mspr4_2025.products_service.entity.StockEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDto {
    private String name;

    private UUID uid;
    
    private int quantity;

    private int totalPrice;

    private StockEntity stock;
}
