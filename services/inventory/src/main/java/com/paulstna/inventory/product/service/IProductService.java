package com.paulstna.inventory.product.service;

import com.paulstna.inventory.product.dto.ProductRequestDTO;
import com.paulstna.inventory.product.dto.ProductResponseDTO;
import com.paulstna.inventory.product.model.Product;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(UUID productId);

    List<ProductResponseDTO> getProductsByCategory(String category);

    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO productRequest);

    void deleteProduct(UUID productId);

    Product getProductByIdOrThrow(UUID productId);
}
