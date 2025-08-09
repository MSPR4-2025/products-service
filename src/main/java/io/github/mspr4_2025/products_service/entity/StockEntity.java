package io.github.mspr4_2025.products_service.entity;


import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Stocks")
public class StockEntity {

        @Id    
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true)
        private UUID uid = UUID.randomUUID();


        @Column(name="name")
        private String productName;

        @Column(name="price")
        private double price;

        @Column(name="stock_inventaire")
        private int stockInventaire;
}
