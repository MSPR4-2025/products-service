package io.github.mspr4_2025.products_service.service;


import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.mapper.ProductMapper;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.model.ProductUpdateDto;
import io.github.mspr4_2025.products_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductsServices {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * @throws ResponseStatusException when no entity exist with the given uid.
     *                                 This exception is handled by the controllers, returning a response with the corresponding http status.
     */
    public ProductEntity getProductByUid(UUID uid) {
        Optional<ProductEntity> entity = productRepository.findByUid(uid);

        if (entity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return entity.get();
    }

    public ProductEntity createProduct(ProductCreateDto productCreateDto) {
        ProductEntity entity = productMapper.fromCreateDto(productCreateDto);

        return productRepository.save(entity);
    }

    public void updateProduct(UUID uid, ProductUpdateDto productUpdateDto) {
        ProductEntity entity = this.getProductByUid(uid);

        productMapper.updateEntityFromDto(productUpdateDto, entity);

        productRepository.save(entity);
    }

    public void deleteProduct(UUID uid) {
        ProductEntity productEntity = this.getProductByUid(uid);

        try {
            productRepository.delete(productEntity);
        } catch (Exception e) {
            log.error("Error deleting order: {}", e.getMessage(), e);
        }
    }

}


