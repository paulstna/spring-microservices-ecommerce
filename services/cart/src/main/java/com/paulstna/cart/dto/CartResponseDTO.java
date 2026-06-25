package com.paulstna.cart.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CartResponseDTO {
    private UUID userId;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;
    private Instant updatedAt;
}