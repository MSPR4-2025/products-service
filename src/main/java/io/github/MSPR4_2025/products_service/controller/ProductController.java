package io.github.MSPR4_2025.products_service.controller;
import org.springframework.stereotype.Controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.github.MSPR4_2025.products_service.entity.products;
import io.github.MSPR4_2025.products_service.service.ProductsServices;


@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductsServices ProductsServices;


    public ProductController(ProductsServices productsServices) {
        this.ProductsServices = productsServices;
    }

    @GetMapping("/{id}")
    public products getById(Long id) {
        return ProductsServices.getProductById(id);
    }
}
