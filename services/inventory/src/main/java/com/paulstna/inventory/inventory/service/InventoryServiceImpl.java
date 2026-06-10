package com.paulstna.inventory.inventory.service;

import com.paulstna.inventory.common.exception.ResourceNotFoundException;
import com.paulstna.inventory.inventory.constants.MessageConstants;
import com.paulstna.inventory.inventory.dto.InventoryRequestDTO;
import com.paulstna.inventory.inventory.dto.InventoryResponseDTO;
import com.paulstna.inventory.inventory.dto.StockReservationDTO;
import com.paulstna.inventory.inventory.mapper.InventoryMapper;
import com.paulstna.inventory.inventory.model.Inventory;
import com.paulstna.inventory.inventory.model.StockReservation;
import com.paulstna.inventory.inventory.model.StockStatus;
import com.paulstna.inventory.inventory.repository.InventoryRepository;
import com.paulstna.inventory.inventory.repository.StockReservationRepository;
import com.paulstna.inventory.product.model.Product;
import com.paulstna.inventory.product.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements IinventoryService {

    private final InventoryRepository inventoryRepository;
    private final IProductService productService;
    private final StockReservationRepository reservationRepository;

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
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock()); // bonus fix
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

        // Idempotencia: si la orden ya tiene reserva, devolver su estado actual.
        Optional<StockReservation> existing = reservationRepository.findByOrderId(stockReservation.getOrderId());
        if (existing.isPresent()) {
            return buildResponse(inventory, product, existing.get().getStatus(), MessageConstants.STOCK_RESERVED);
        }

        StockReservationDTO response = InventoryMapper.toStockReservation(inventory, product);
        if (inventory.getAvailableStock() < stockReservation.getQuantity()) {
            response.setStatus(StockStatus.INSUFFICIENT);
            response.setMessage(MessageConstants.INSUFFICIENT_STOCK);
            return response;
        }

        inventory.setReservedStock(inventory.getReservedStock() + stockReservation.getQuantity());
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock());
        inventoryRepository.save(inventory);

        StockReservation reservation = new StockReservation();
        reservation.setOrderId(stockReservation.getOrderId());
        reservation.setProductId(stockReservation.getProductId());
        reservation.setQuantity(stockReservation.getQuantity());
        reservation.setStatus(StockStatus.RESERVED);
        reservationRepository.save(reservation);

        response.setStatus(StockStatus.RESERVED);
        response.setMessage(MessageConstants.STOCK_RESERVED);
        return response;
    }

    @Transactional
    @Override
    public StockReservationDTO releaseStock(StockReservationDTO stockReservation) {
        StockReservation reservation = getReservationByOrderIdOrThrow(stockReservation.getOrderId());
        Product product = productService.getProductByIdOrThrow(reservation.getProductId());
        Inventory inventory = getInventoryByProductIdOrThrow(reservation.getProductId());

        if (reservation.getStatus() == StockStatus.RELEASED) {          // idempotente
            return buildResponse(inventory, product, StockStatus.RELEASED, MessageConstants.STOCK_RELEASED);
        }
        if (reservation.getStatus() != StockStatus.RESERVED) {
            throw new IllegalStateException("Cannot release a reservation in status " + reservation.getStatus());
        }

        inventory.setReservedStock(inventory.getReservedStock() - reservation.getQuantity());
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock());
        inventoryRepository.save(inventory);

        reservation.setStatus(StockStatus.RELEASED);
        reservationRepository.save(reservation);

        return buildResponse(inventory, product, StockStatus.RELEASED, MessageConstants.STOCK_RELEASED);
    }

    @Transactional
    @Override
    public StockReservationDTO confirmStock(StockReservationDTO stockReservation) {
        StockReservation reservation = getReservationByOrderIdOrThrow(stockReservation.getOrderId());
        Product product = productService.getProductByIdOrThrow(reservation.getProductId());
        Inventory inventory = getInventoryByProductIdOrThrow(reservation.getProductId());

        if (reservation.getStatus() == StockStatus.CONFIRMED) {         // idempotente
            return buildResponse(inventory, product, StockStatus.CONFIRMED, MessageConstants.STOCK_CONFIRMED);
        }
        if (reservation.getStatus() != StockStatus.RESERVED) {
            throw new IllegalStateException("Cannot confirm a reservation in status " + reservation.getStatus());
        }

        inventory.setTotalStock(inventory.getTotalStock() - reservation.getQuantity());
        inventory.setReservedStock(inventory.getReservedStock() - reservation.getQuantity());
        inventory.setAvailableStock(inventory.getTotalStock() - inventory.getReservedStock());
        inventoryRepository.save(inventory);

        reservation.setStatus(StockStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return buildResponse(inventory, product, StockStatus.CONFIRMED, MessageConstants.STOCK_CONFIRMED);
    }

    private StockReservationDTO buildResponse(Inventory inventory, Product product,
                                              StockStatus status, String message) {
        StockReservationDTO response = InventoryMapper.toStockReservation(inventory, product);
        response.setStatus(status);
        response.setMessage(message);
        return response;
    }

    private Inventory getInventoryByProductIdOrThrow(UUID productId) {
        return inventoryRepository.findByProductId(productId).orElseThrow(   // Fix 1
                () -> new ResourceNotFoundException("Invalid product id: " + productId));
    }

    private StockReservation getReservationByOrderIdOrThrow(UUID orderId) {
        return reservationRepository.findByOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException("No reservation found for order id: " + orderId));
    }
}