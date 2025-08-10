package io.github.mspr4_2025.products_service.controller;

import io.github.mspr4_2025.products_service.entity.StockEntity;
import io.github.mspr4_2025.products_service.service.StockService;
import io.github.mspr4_2025.products_service.model.StockDto;
import io.github.mspr4_2025.products_service.model.StockCreateDto;
import io.github.mspr4_2025.products_service.mapper.StockMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;
    private final StockMapper stockMapper;

    @GetMapping("/")
    public ResponseEntity<List<StockDto>> listInventory() {
        List<StockEntity> stockEntities = stockService.getAllInventory();
        return ResponseEntity.ok(stockMapper.fromEntities(stockEntities));
    }

    @PostMapping("/")
    public ResponseEntity<StockDto> createInventory(@RequestBody StockCreateDto stockCreate) {
        StockEntity createdEntity = stockService.createInventory(stockCreate);
        URI stockUri = MvcUriComponentsBuilder
            .fromMethodCall(MvcUriComponentsBuilder
                .on(getClass())
                .getWarehouse(createdEntity.getUid()))
            .build()
            .toUri();

        return ResponseEntity.created(stockUri).build();
    }

    @PutMapping("/{uid}")
    public ResponseEntity<StockDto> updateStock(@PathVariable UUID uid, @RequestBody StockCreateDto stockUpdate) {
        StockEntity updatedEntity = stockService.updateInventory(uid, stockUpdate);
        return ResponseEntity.ok(stockMapper.fromEntity(updatedEntity));
    }

    @GetMapping("/{uid}")
    public StockDto getWarehouse(@PathVariable UUID uid) {
        return stockService.getInventoryById(uid)
                .map(stockMapper::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));
    }

    @DeleteMapping("/{uid}")
    public void deleteStock(@PathVariable UUID uid) {
        stockService.deleteInventory(uid);
    }

}