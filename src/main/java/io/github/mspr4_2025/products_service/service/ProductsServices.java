package io.github.mspr4_2025.products_service.service;


import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.entity.StockEntity;
import io.github.mspr4_2025.products_service.mapper.ProductMapper;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.repository.ProductRepository;
import io.github.mspr4_2025.products_service.repository.StockRepository;
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

    private final StockRepository stockRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<ProductEntity> getProductById(UUID uid) {
        return productRepository.findByUid(uid);
    }

    public ProductEntity createProduct(ProductCreateDto productCreateDto) {

        ProductEntity entity = productMapper.fromCreateDto(productCreateDto);
        StockEntity stock = stockRepository.findByUid(productCreateDto.getStockUid()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if(stock.getStockInventaire() < productCreateDto.getQuantity()){
            new ResponseStatusException(HttpStatus.CONFLICT, "Stock quantity not sufficient");
        }
        entity.setStock(stock);
        entity.setTotalPrice(productCreateDto.getQuantity() * stock.getPrice());
        return productRepository.save(entity);
    }

    public ProductEntity updateProduct(UUID uid, ProductCreateDto productUpdateDto) {
        return productRepository.findByUid(uid)
                .map(existingProduct -> {
                    existingProduct.setTotalPrice(existingProduct.getStock().getPrice() * productUpdateDto.getQuantity());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public void deleteProduct(UUID uid) {
        if (!productRepository.existsByUid(uid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteByUid(uid);
    }

}


