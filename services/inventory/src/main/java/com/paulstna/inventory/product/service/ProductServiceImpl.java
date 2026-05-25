package com.paulstna.inventory.product.service;

import com.paulstna.inventory.common.exception.ResourceNotFoundException;
import com.paulstna.inventory.product.dto.ProductRequestDTO;
import com.paulstna.inventory.product.dto.ProductResponseDTO;
import com.paulstna.inventory.product.mapper.ProductMapper;
import com.paulstna.inventory.product.model.Product;
import com.paulstna.inventory.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAllByActiveTrue()
                .stream()
                .map(ProductMapper::toProductResponse)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(UUID productId) {
        Product product = getProductByIdOrThrow(productId);
        return ProductMapper.toProductResponse(product);
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        return productRepository.findAllByCategoryAndActiveTrue(category)
                .stream()
                .map(ProductMapper::toProductResponse)
                .toList();
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        Product product = ProductMapper.toEntity(productRequestDTO, new Product());
        return ProductMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO productRequest) {
        Product product = getProductByIdOrThrow(productId);
        product = ProductMapper.toEntity(productRequest, product);
        return ProductMapper.toProductResponse(productRepository.save(product));
    }

    @Override
    public void deleteProduct(UUID productId) {
        Product product = getProductByIdOrThrow(productId);
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public Product getProductByIdOrThrow(UUID productId) {
        return productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
