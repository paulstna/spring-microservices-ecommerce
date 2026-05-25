package com.paulstna.inventory.inventory.dto;

import com.paulstna.inventory.inventory.model.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponseDTO {
    private UUID inventoryId;
    private UUID productId;
    private String productName;
    private StockStatus stockStatus;
    private Integer totalStock;
    private Integer reservedStock;
    private Integer availableStock;
    private Instant updatedAt;
}
