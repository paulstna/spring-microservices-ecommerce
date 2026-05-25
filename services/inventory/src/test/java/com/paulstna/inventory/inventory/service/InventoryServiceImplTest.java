package com.paulstna.inventory.inventory.service;

import com.paulstna.inventory.common.exception.ResourceNotFoundException;
import com.paulstna.inventory.inventory.constants.MessageConstants;
import com.paulstna.inventory.inventory.dto.InventoryRequestDTO;
import com.paulstna.inventory.inventory.dto.InventoryResponseDTO;
import com.paulstna.inventory.inventory.dto.StockReservationDTO;
import com.paulstna.inventory.inventory.model.Inventory;
import com.paulstna.inventory.inventory.model.StockStatus;
import com.paulstna.inventory.inventory.repository.InventoryRepository;
import com.paulstna.inventory.product.model.Product;
import com.paulstna.inventory.product.service.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private IProductService productService;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private UUID productId;
    private UUID orderId;
    private Product product;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setName("Camiseta Nike");
        product.setPrice(new BigDecimal("29.99"));
        product.setActive(true);

        inventory = new Inventory();
        inventory.setId(UUID.randomUUID());
        inventory.setProductId(productId);
        inventory.setTotalStock(100);
        inventory.setReservedStock(10);
        inventory.setAvailableStock(90);
    }

    // ─── getStockByProductId ───────────────────────────────────────────────────

    @Test
    void getStockByProductId_returnsInventoryResponse() {
        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        InventoryResponseDTO result = inventoryService.getStockByProductId(productId);

        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo("Camiseta Nike");
        assertThat(result.getTotalStock()).isEqualTo(100);
        assertThat(result.getAvailableStock()).isEqualTo(90);
    }

    @Test
    void getStockByProductId_throwsResourceNotFoundException_whenProductNotFound() {
        when(productService.getProductByIdOrThrow(productId))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        assertThatThrownBy(() -> inventoryService.getStockByProductId(productId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found");
    }

    @Test
    void getStockByProductId_throwsResourceNotFoundException_whenInventoryNotFound() {
        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getStockByProductId(productId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── createInventory ───────────────────────────────────────────────────────

    @Test
    void createInventory_savesAndReturnsInventory() {
        InventoryRequestDTO request = InventoryRequestDTO.builder()
                .productId(productId)
                .quantity(50)
                .build();

        Inventory saved = new Inventory();
        saved.setProductId(productId);
        saved.setTotalStock(50);
        saved.setReservedStock(0);
        saved.setAvailableStock(50);

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(saved);

        InventoryResponseDTO result = inventoryService.createInventory(request);

        assertThat(result.getTotalStock()).isEqualTo(50);
        assertThat(result.getReservedStock()).isEqualTo(0);
        assertThat(result.getAvailableStock()).isEqualTo(50);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    // ─── addInventoryStock ─────────────────────────────────────────────────────

    @Test
    void addInventoryStock_increasesTotalStock() {
        InventoryRequestDTO request = InventoryRequestDTO.builder()
                .productId(productId)
                .quantity(20)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryResponseDTO result = inventoryService.addInventoryStock(productId, request);

        assertThat(result.getTotalStock()).isEqualTo(120); // 100 + 20
        verify(inventoryRepository).save(inventory);
    }

    // ─── checkStock ────────────────────────────────────────────────────────────

    @Test
    void checkStock_returnsAvailable_whenStockIsSufficient() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .quantity(5)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        InventoryResponseDTO result = inventoryService.checkStock(request);

        assertThat(result.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
    }

    @Test
    void checkStock_returnsInsufficient_whenStockIsNotEnough() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .quantity(999)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        InventoryResponseDTO result = inventoryService.checkStock(request);

        assertThat(result.getStockStatus()).isEqualTo(StockStatus.INSUFFICIENT);
    }

    // ─── reserveStock ──────────────────────────────────────────────────────────

    @Test
    void reserveStock_reservesCorrectly_whenStockIsSufficient() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(10)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        StockReservationDTO result = inventoryService.reserveStock(request);

        assertThat(result.getStatus()).isEqualTo(StockStatus.RESERVED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.STOCK_RESERVED);
        assertThat(result.getAvailableStock()).isEqualTo(80); // 90 - 10
        assertThat(inventory.getReservedStock()).isEqualTo(20); // 10 + 10
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void reserveStock_returnsInsufficient_whenStockIsNotEnough() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(999)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        StockReservationDTO result = inventoryService.reserveStock(request);

        assertThat(result.getStatus()).isEqualTo(StockStatus.INSUFFICIENT);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.INSUFFICIENT_STOCK);
        verify(inventoryRepository, never()).save(any()); // no se guarda nada
    }

    // ─── releaseStock ──────────────────────────────────────────────────────────

    @Test
    void releaseStock_releasesCorrectly_whenReservedStockIsSufficient() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(5)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        StockReservationDTO result = inventoryService.releaseStock(request);

        assertThat(result.getStatus()).isEqualTo(StockStatus.RELEASED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.STOCK_RELEASED);
        assertThat(inventory.getReservedStock()).isEqualTo(5);  // 10 - 5
        assertThat(inventory.getAvailableStock()).isEqualTo(95); // 100 - 5
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void releaseStock_throwsIllegalStateException_whenReservedStockIsInsufficient() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(999)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> inventoryService.releaseStock(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not Enough Stock to release stock");

        verify(inventoryRepository, never()).save(any());
    }

    // ─── confirmStock ──────────────────────────────────────────────────────────

    @Test
    void confirmStock_discountsStockCorrectly() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(10)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        StockReservationDTO result = inventoryService.confirmStock(request);

        assertThat(result.getStatus()).isEqualTo(StockStatus.CONFIRMED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.STOCK_CONFIRMED);
        assertThat(inventory.getTotalStock()).isEqualTo(90);    // 100 - 10
        assertThat(inventory.getReservedStock()).isEqualTo(0);  // 10 - 10
        assertThat(inventory.getAvailableStock()).isEqualTo(90); // 90 - 0
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void confirmStock_throwsResourceNotFoundException_whenInventoryNotFound() {
        StockReservationDTO request = StockReservationDTO.builder()
                .productId(productId)
                .quantity(5)
                .build();

        when(productService.getProductByIdOrThrow(productId)).thenReturn(product);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.confirmStock(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}