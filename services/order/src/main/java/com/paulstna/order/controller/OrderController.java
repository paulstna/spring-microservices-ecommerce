package com.paulstna.order.controller;

import com.paulstna.order.dto.OrderResponseDTO;
import com.paulstna.order.dto.PlaceOrderRequestDTO;
import com.paulstna.order.dto.UpdateOrderStatusRequestDTO;
import com.paulstna.order.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/{version}/orders", version = "v1")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid PlaceOrderRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.placeOrder(request));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUserId(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    // Used for testing purposes only.
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID orderId,
            @RequestBody @Valid UpdateOrderStatusRequestDTO request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(
                orderId, request.getStatus(), request.getFailureReason()));
    }
}