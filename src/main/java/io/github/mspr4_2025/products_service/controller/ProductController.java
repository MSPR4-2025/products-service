package io.github.mspr4_2025.products_service.controller;

import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.mapper.ProductMapper;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.model.ProductDto;
import io.github.mspr4_2025.products_service.service.ProductsServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductsServices productsServices;
    private final ProductMapper productMapper;

    @GetMapping("/")
    public ResponseEntity<List<ProductDto>> listProducts() {
        List<ProductEntity> productEntities = productsServices.getAllProducts();
        return ResponseEntity.ok(productMapper.fromEntities(productEntities));
    }

    @PostMapping("/")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductCreateDto productCreate) {
        ProductEntity createdEntity = productsServices.createProduct(productCreate);

        // Get the url to GET the created Product
        URI productUri = MvcUriComponentsBuilder
            .fromMethodCall(MvcUriComponentsBuilder
                .on(getClass())
                .getProduct(createdEntity.getUid()))
            .build()
            .toUri();

        return ResponseEntity.created(productUri).build();
    }

    @GetMapping("/{uid}")
    public ProductDto getProduct(@PathVariable UUID uid) {
        return productsServices.getProductById(uid)
            .map(productMapper::fromEntity)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @DeleteMapping("/{uid}")
    public void deleteProduct(@PathVariable UUID uid) {
        productsServices.deleteProduct(uid);
    }
}
