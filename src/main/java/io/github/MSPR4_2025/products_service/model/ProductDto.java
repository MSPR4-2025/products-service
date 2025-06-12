package io.github.MSPR4_2025.products_service.model;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Getter
@Setter
public class ProductDto extends ProductCreateDto {
    private UUID uid;
}
