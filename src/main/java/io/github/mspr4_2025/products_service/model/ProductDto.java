package io.github.mspr4_2025.products_service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductDto extends ProductCreateDto {
    private UUID uid;
    private int quantity;
    private UUID stockUid;
    private String name;
    private double totalPrice;
}
