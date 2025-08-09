package io.github.mspr4_2025.products_service.model;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto {
    private UUID uid;

    private int stockInventaire;
}
