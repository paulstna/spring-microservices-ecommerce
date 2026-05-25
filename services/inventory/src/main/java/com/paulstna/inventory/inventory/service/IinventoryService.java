package com.paulstna.inventory.inventory.service;

import com.paulstna.inventory.inventory.dto.InventoryRequestDTO;
import com.paulstna.inventory.inventory.dto.InventoryResponseDTO;
import com.paulstna.inventory.inventory.dto.StockReservationDTO;

import java.util.UUID;

public interface IinventoryService {
    InventoryResponseDTO getStockByProductId(UUID productId);

    InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequest);

    InventoryResponseDTO addInventoryStock(UUID productId, InventoryRequestDTO inventoryRequest);

    InventoryResponseDTO checkStock(StockReservationDTO stockReservation);

    StockReservationDTO reserveStock(StockReservationDTO stockReservation);

    StockReservationDTO releaseStock(StockReservationDTO stockReservation);

    StockReservationDTO confirmStock(StockReservationDTO stockReservation);
}
