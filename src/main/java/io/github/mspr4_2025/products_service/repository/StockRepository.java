package io.github.mspr4_2025.products_service.repository;

import io.github.mspr4_2025.products_service.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockRepository extends JpaRepository<StockEntity, Long> {
    Optional<StockEntity> findByUid(UUID uid);
    void deleteByUid(UUID uid);
}
