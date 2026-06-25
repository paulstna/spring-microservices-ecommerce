package com.paulstna.cart.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Cart {
    private UUID userId;
    private List<CartItem> items = new ArrayList<>();
    private Instant updatedAt;
}