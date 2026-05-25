package com.paulstna.inventory.inventory.service;

import com.paulstna.inventory.common.exception.ResourceNotFoundException;
import com.paulstna.inventory.inventory.constants.MessageConstants;
import com.paulstna.inventory.inventory.dto.InventoryRequestDTO;
import com.paulstna.inventory.inventory.dto.InventoryResponseDTO;
import com.paulstna.inventory.inventory.dto.StockReservationDTO;
import com.paulstna.inventory.inventory.mapper.InventoryMapper;
import com.paulstna.inventory.inventory.model.Inventory;
import com.paulstna.inventory.inventory.model.StockStatus;
import com.paulstna.inventory.inventory.repository.InventoryRepository;
import com.paulstna.inventory.product.model.Product;
import com.paulstna.inventory.product.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements IinventoryService {

    private final InventoryRepository inventoryRepository;
    private final IProductService productService;

    @Override
    public InventoryResponseDTO getStockByProductId(UUID productId) {
        Product product = productService.getProductByIdOrThrow(productId);
        Inventory inventory = getInventoryByProductIdOrThrow(productId);

        return InventoryMapper.toInventoryResponse(inventory, product);
    }

    @Override
    public InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequest) {
        Product product = productService.getProductByIdOrThrow(inventoryRequest.getProductId());
        Inventory inventory = InventoryMapper.toEntity(inventoryRequest, new Inventory());

        return InventoryMapper.toInventoryResponse(inventoryRepository.save(inventory), product);
    }

    @Override
    public InventoryResponseDTO addInventoryStock(UUID productId, InventoryRequestDTO inventoryRequest) {
        Product product = productService.getProductByIdOrThrow(productId);
        Inventory inventory = getInventoryByProductIdOrThrow(productId);

        inventory.setTotalStock(inventory.getTotalStock() + inventoryRequest.getQuantity());
        return InventoryMapper.toInventoryResponse(inventoryRepository.save(inventory), product);
    }

    @Override
    public InventoryResponseDTO checkStock(StockReservationDTO stockReservation) {
        Product product = productService.getProductByIdOrThrow(stockReservation.getProductId());
        Inventory inventory = getInventoryByProductIdOrThrow(stockReservation.getProductId());

        InventoryResponseDTO inventoryResponseDTO = InventoryMapper.toInventoryResponse(inventory, product);
        inventoryResponseDTO.setStockStatus(
                inventory.getAvailableStock() >= stockReservation.getQuantity()
                        ? StockStatus.AVAILABLE : StockStatus.INSUFFICIENT
        );
        return inventoryResponseDTO;
    }

    @Transactional
    @Override
    public StockReservationDTO reserveStock(StockReservationDTO stockReservation) {
        Product product = productService.getProductByIdOrThrow(stockReservation.getProductId());
        Inventory inventory = getInventoryByProductIdOrThrow(stockReservation.getProductId());

        StockReservationDTO stockReservationDTO = InventoryMapper.toStockReservation(inventory, product);

        if (inventory.getAvailableStock() < stockReservation.getQuantity()) {
            stockReservationDTO.setStatus(StockStatus.INSUFFICIENT);
            stockReservationDTO.setMessage(MessageConstants.INSUFFICIENT_STOCK);
            return stockReservationDTO;
        }

        inventory.setReservedStock(inventory.getReservedStock() + stockReservation.getQuantity());
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock());
        inventoryRepository.save(inventory);

        stockReservationDTO.setAvailableStock(inventory.getAvailableStock());
        stockReservationDTO.setStatus(StockStatus.RESERVED);
        stockReservationDTO.setMessage(MessageConstants.STOCK_RESERVED);

        return stockReservationDTO;
    }

    @Transactional
    @Override
    public StockReservationDTO releaseStock(StockReservationDTO stockReservation) {
        Product product = productService.getProductByIdOrThrow(stockReservation.getProductId());
        Inventory inventory = getInventoryByProductIdOrThrow(stockReservation.getProductId());

        if (inventory.getReservedStock() < stockReservation.getQuantity()) {
            throw new IllegalStateException("Not Enough Stock to release stock");
        }

        inventory.setReservedStock(inventory.getReservedStock() - stockReservation.getQuantity());
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock());
        inventoryRepository.save(inventory);

        StockReservationDTO stockReservationDTO = InventoryMapper.toStockReservation(inventory, product);
        stockReservationDTO.setStatus(StockStatus.RELEASED);
        stockReservationDTO.setMessage(MessageConstants.STOCK_RELEASED);

        return stockReservationDTO;
    }

    @Transactional
    @Override
    public StockReservationDTO confirmStock(StockReservationDTO stockReservation) {
        Product product = productService.getProductByIdOrThrow(stockReservation.getProductId());
        Inventory inventory = getInventoryByProductIdOrThrow(stockReservation.getProductId());

        inventory.setTotalStock(inventory.getTotalStock() - stockReservation.getQuantity());
        inventory.setReservedStock(inventory.getReservedStock() - stockReservation.getQuantity());
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock());
        inventoryRepository.save(inventory);

        StockReservationDTO stockReservationDTO = InventoryMapper.toStockReservation(inventory, product);
        stockReservationDTO.setStatus(StockStatus.CONFIRMED);
        stockReservationDTO.setMessage(MessageConstants.STOCK_CONFIRMED);

        return stockReservationDTO;
    }

    private Inventory getInventoryByProductIdOrThrow(UUID productId) {
        return inventoryRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Invalid product id: " + productId));
    }

}
