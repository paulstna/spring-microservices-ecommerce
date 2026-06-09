package com.paulstna.delivery.controller;

import com.paulstna.delivery.dto.DeliveryOperationDTO;
import com.paulstna.delivery.dto.DeliveryRequestDTO;
import com.paulstna.delivery.dto.DeliveryResponseDTO;
import com.paulstna.delivery.service.IDeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/{version}/deliveries", version = "v1")
@RequiredArgsConstructor
public class DeliveryController {

    private final IDeliveryService deliveryService;

    @GetMapping("/{orderId}")
    public ResponseEntity<DeliveryResponseDTO> getShipmentByOrderId(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(deliveryService.getShipmentByOrderId(orderId));
    }

    @PostMapping
    public ResponseEntity<DeliveryOperationDTO> createShipment(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid DeliveryRequestDTO deliveryRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(deliveryService.createShipment(deliveryRequest));
    }

    @PostMapping("/cancel")
    public ResponseEntity<DeliveryOperationDTO> cancelShipment(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid DeliveryOperationDTO operation) {
        return ResponseEntity.ok(deliveryService.cancelShipment(operation));
    }
}