package com.paulstna.inventory.inventory.dto;

import com.paulstna.inventory.inventory.model.StockStatus;
import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockReservationDTO {

    // REQUEST
    private UUID productId;
    private Integer quantity;
    private UUID orderId;

    // RESPONSE
    private StockStatus status;
    private Integer availableStock;
    private String message;
}
