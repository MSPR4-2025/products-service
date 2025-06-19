package io.github.mspr4_2025.products_service.service;


import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.mapper.ProductMapper;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductsServices {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<ProductEntity> getProductById(UUID uid) {
        return productRepository.findByUid(uid);
    }

    public ProductEntity createProduct(ProductCreateDto productCreateDto) {

        ProductEntity entity = productMapper.fromCreateDto(productCreateDto);
        return productRepository.save(entity);
    }

    public void deleteProduct(UUID uid) {
        if (!productRepository.existsByUid(uid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteByUid(uid);
    }

}


