package com.paulstna.inventory.product.controller;

import com.paulstna.inventory.product.dto.ProductRequestDTO;
import com.paulstna.inventory.product.dto.ProductResponseDTO;
import com.paulstna.inventory.product.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/{version}/products", version = "v1")
public class ProductController {

    private final IProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId
    ) {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid ProductRequestDTO productRequestDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(productRequestDTO));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID productId,
            @RequestBody @Valid ProductRequestDTO productRequest) {
        return ResponseEntity.ok(productService.updateProduct(productId, productRequest));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
