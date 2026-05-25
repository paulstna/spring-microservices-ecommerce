package com.paulstna.inventory.inventory.mapper;

import com.paulstna.inventory.inventory.dto.InventoryRequestDTO;
import com.paulstna.inventory.inventory.dto.InventoryResponseDTO;
import com.paulstna.inventory.inventory.dto.StockReservationDTO;
import com.paulstna.inventory.inventory.model.Inventory;
import com.paulstna.inventory.product.model.Product;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InventoryMapper {

    public InventoryResponseDTO toInventoryResponse(Inventory inventory, Product product) {
        return InventoryResponseDTO.builder()
                .inventoryId(inventory.getId())
                .productId(product.getId())
                .productName(product.getName())
                .totalStock(inventory.getTotalStock())
                .reservedStock(inventory.getReservedStock())
                .availableStock(inventory.getAvailableStock())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }

    public Inventory toEntity(InventoryRequestDTO dto, Inventory inventory) {
        inventory.setProductId(dto.getProductId());
        inventory.setTotalStock(dto.getQuantity());
        inventory.setReservedStock(0);
        inventory.setAvailableStock(dto.getQuantity());
        return inventory;
    }

    public StockReservationDTO toStockReservation(Inventory inventory, Product product) {
        return StockReservationDTO.builder()
                .productId(product.getId())
                .availableStock(inventory.getAvailableStock())
                .build();
    }
}
