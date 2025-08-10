package io.github.mspr4_2025.products_service.service;

import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.entity.StockEntity;
import io.github.mspr4_2025.products_service.model.StockCreateDto;
import io.github.mspr4_2025.products_service.repository.ProductRepository;
import io.github.mspr4_2025.products_service.repository.StockRepository;
import io.github.mspr4_2025.products_service.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    
    public List<StockEntity> getAllInventory() {
        return stockRepository.findAll();
    }

    
    public StockEntity createInventory(StockCreateDto dto) {
        StockEntity entity = new StockEntity();
        entity.setStockInventaire(dto.getStockInventaire());
        entity.setPrice(dto.getPrice());
        entity.setProductName(dto.getProductName());
        
        
        return stockRepository.save(entity);
    }


    public StockEntity updateInventory(UUID uid, StockCreateDto dto) {
        StockEntity entity = stockRepository.findByUid(uid)
            .orElseThrow(() -> new IllegalArgumentException("Stock not found"));
        entity.setStockInventaire(dto.getStockInventaire());
        entity.setPrice(dto.getPrice());
        entity.setProductName(dto.getProductName());
        return stockRepository.save(entity);
    }


    public Optional<StockEntity> getInventoryById(UUID uid) {
        return stockRepository.findByUid(uid);
    }

    public List<ProductEntity> getAllProductsByStockUid(UUID uid){
        return productRepository.findByStockUid(uid);
    }


    public void deleteInventory(UUID uid) {
        stockRepository.deleteByUid(uid);
    }
}
