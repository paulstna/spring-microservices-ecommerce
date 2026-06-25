package com.paulstna.cart.service;

import com.paulstna.cart.dto.CartItemRequestDTO;
import com.paulstna.cart.dto.CartResponseDTO;

import java.util.UUID;

public interface ICartService {
    CartResponseDTO getCart(UUID userId);

    CartResponseDTO addItem(UUID userId, CartItemRequestDTO request);

    CartResponseDTO updateItemQuantity(UUID userId, UUID productId, int quantity);

    CartResponseDTO removeItem(UUID userId, UUID productId);

    void clearCart(UUID userId);
}