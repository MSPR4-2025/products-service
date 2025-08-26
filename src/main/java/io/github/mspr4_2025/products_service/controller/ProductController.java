package io.github.mspr4_2025.products_service.controller;

import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.model.ProductUpdateDto;
import io.github.mspr4_2025.products_service.service.ProductsServices;
import io.github.mspr4_2025.products_service.model.ProductDto;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.mapper.ProductMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<ProductDto>> listProducts() {
        List<ProductEntity> productEntities = productsServices.getAllProducts();

        return ResponseEntity.ok(productMapper.fromEntities(productEntities));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductCreateDto ProductCreate) {
        ProductEntity createdEntity = productsServices.createProduct(ProductCreate);

        // Get the url to GET the created Product
        URI ProductUri = MvcUriComponentsBuilder
            .fromMethodCall(MvcUriComponentsBuilder
                .on(getClass())
                .getProduct(createdEntity.getUid()))
            .build()
            .toUri();

        return ResponseEntity.created(ProductUri).build();
    }

    @PutMapping("/{uid}")
    public ResponseEntity<Void> updateProduct(@PathVariable UUID uid, @RequestBody ProductUpdateDto productUpdate) {
        productsServices.updateProduct(uid, productUpdate);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{uid}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID uid) {
        ProductEntity productEntity = productsServices.getProductByUid(uid);

        return ResponseEntity.ok(productMapper.fromEntity(productEntity));
    }

    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID uid) {
        productsServices.deleteProduct(uid);

        return ResponseEntity.noContent().build();
    }
}
