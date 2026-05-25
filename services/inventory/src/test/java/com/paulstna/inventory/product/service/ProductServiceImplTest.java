package com.paulstna.inventory.product.service;

import com.paulstna.inventory.common.exception.ResourceNotFoundException;
import com.paulstna.inventory.product.dto.ProductRequestDTO;
import com.paulstna.inventory.product.dto.ProductResponseDTO;
import com.paulstna.inventory.product.model.Product;
import com.paulstna.inventory.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private UUID productId;
    private Product product;
    private ProductRequestDTO productRequest;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setName("Camiseta Nike");
        product.setDescription("Camiseta deportiva");
        product.setPrice(new BigDecimal("29.99"));
        product.setCategory("ropa");
        product.setImageUrl("https://img.com/nike.jpg");
        product.setActive(true);

        productRequest = ProductRequestDTO.builder()
                .name("Camiseta Nike")
                .description("Camiseta deportiva")
                .price(new BigDecimal("29.99"))
                .category("ropa")
                .imageUrl("https://img.com/nike.jpg")
                .build();
    }

    // ─── getAllProducts ────────────────────────────────────────────────────────

    @Test
    void getAllProducts_returnsActiveProducts() {
        when(productRepository.findAllByActiveTrue()).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Camiseta Nike");
        verify(productRepository).findAllByActiveTrue();
    }

    @Test
    void getAllProducts_returnsEmptyList_whenNoActiveProducts() {
        when(productRepository.findAllByActiveTrue()).thenReturn(List.of());

        List<ProductResponseDTO> result = productService.getAllProducts();

        assertThat(result).isEmpty();
    }

    // ─── getProductById ────────────────────────────────────────────────────────

    @Test
    void getProductById_returnsProduct_whenExists() {
        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));

        ProductResponseDTO result = productService.getProductById(productId);

        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getName()).isEqualTo("Camiseta Nike");
    }

    @Test
    void getProductById_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    // ─── getProductsByCategory ─────────────────────────────────────────────────

    @Test
    void getProductsByCategory_returnsFilteredProducts() {
        when(productRepository.findAllByCategoryAndActiveTrue("ropa")).thenReturn(List.of(product));

        List<ProductResponseDTO> result = productService.getProductsByCategory("ropa");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("ropa");
    }

    @Test
    void getProductsByCategory_returnsEmptyList_whenNoneMatch() {
        when(productRepository.findAllByCategoryAndActiveTrue("electronica")).thenReturn(List.of());

        List<ProductResponseDTO> result = productService.getProductsByCategory("electronica");

        assertThat(result).isEmpty();
    }

    // ─── createProduct ─────────────────────────────────────────────────────────

    @Test
    void createProduct_savesAndReturnsProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.createProduct(productRequest);

        assertThat(result.getName()).isEqualTo("Camiseta Nike");
        assertThat(result.getPrice()).isEqualByComparingTo("29.99");
        verify(productRepository).save(any(Product.class));
    }

    // ─── updateProduct ─────────────────────────────────────────────────────────

    @Test
    void updateProduct_updatesAndReturnsProduct() {
        ProductRequestDTO updateRequest = ProductRequestDTO.builder()
                .name("Camiseta Adidas")
                .description("Camiseta deportiva actualizada")
                .price(new BigDecimal("39.99"))
                .category("ropa")
                .imageUrl("https://img.com/adidas.jpg")
                .build();

        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponseDTO result = productService.updateProduct(productId, updateRequest);

        assertThat(result.getName()).isEqualTo("Camiseta Adidas");
        assertThat(result.getPrice()).isEqualByComparingTo("39.99");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateProduct(productId, productRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── deleteProduct ─────────────────────────────────────────────────────────

    @Test
    void deleteProduct_setsActiveFalse() {
        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.deleteProduct(productId);

        assertThat(product.getActive()).isFalse();
        verify(productRepository).save(product);
    }

    @Test
    void deleteProduct_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getProductByIdOrThrow ─────────────────────────────────────────────────

    @Test
    void getProductByIdOrThrow_returnsProduct_whenActiveAndExists() {
        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.of(product));

        Product result = productService.getProductByIdOrThrow(productId);

        assertThat(result.getId()).isEqualTo(productId);
        assertThat(result.getActive()).isTrue();
    }

    @Test
    void getProductByIdOrThrow_throwsResourceNotFoundException_whenInactiveOrNotFound() {
        when(productRepository.findByIdAndActiveTrue(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductByIdOrThrow(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");
    }
}