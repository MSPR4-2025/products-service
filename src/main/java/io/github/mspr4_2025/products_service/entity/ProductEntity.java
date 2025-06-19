package io.github.mspr4_2025.products_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "product")
@Getter
@Setter
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private UUID uid = UUID.randomUUID();

    private String name;

}
