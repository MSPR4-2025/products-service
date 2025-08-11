package io.github.mspr4_2025.products_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "product")
@Table(name = "products")
@Getter
@Setter
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private UUID uid = UUID.randomUUID();

    private int quantity;

    @Column(name="total_price")
    private double totalPrice;

    @OneToOne
    private StockEntity stock;

    private UUID orderUid;
}
