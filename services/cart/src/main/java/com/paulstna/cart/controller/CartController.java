package com.paulstna.cart.controller;

import com.paulstna.cart.dto.CartItemRequestDTO;
import com.paulstna.cart.dto.CartResponseDTO;
import com.paulstna.cart.dto.UpdateQuantityRequestDTO;
import com.paulstna.cart.service.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/{version}/carts", version = "v1")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponseDTO> addItem(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID userId,
            @RequestBody @Valid CartItemRequestDTO request) {
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> updateQuantity(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID userId,
            @PathVariable UUID productId,
            @RequestBody @Valid UpdateQuantityRequestDTO request) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, productId, request.getQuantity()));
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeItem(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID userId,
            @PathVariable UUID productId) {
        return ResponseEntity.ok(cartService.removeItem(userId, productId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}