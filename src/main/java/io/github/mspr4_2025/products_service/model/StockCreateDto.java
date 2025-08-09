package io.github.mspr4_2025.products_service.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockCreateDto {
    private UUID productUUID;

    private int stockInventaire;
}
