package io.github.mspr4_2025.products_service.repository;

import io.github.mspr4_2025.products_service.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByUid(UUID id);

    Optional<ProductEntity> deleteByUid(UUID id);

    boolean existsByUid(UUID id);
}
