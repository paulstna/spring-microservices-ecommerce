package com.paulstna.inventory.inventory.controller;

import com.paulstna.inventory.inventory.dto.InventoryRequestDTO;
import com.paulstna.inventory.inventory.dto.InventoryResponseDTO;
import com.paulstna.inventory.inventory.dto.StockReservationDTO;
import com.paulstna.inventory.inventory.service.IinventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/{version}/inventory", version = "v1")
@RequiredArgsConstructor
public class InventoryController {

    private final IinventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> getStockByProductId(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID productId) {
        return ResponseEntity
                .ok(inventoryService.getStockByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid InventoryRequestDTO inventoryRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(inventoryService.createInventory(inventoryRequest));
    }

    @PutMapping("/{productId}/add")
    public ResponseEntity<InventoryResponseDTO> addStock(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID productId,
            @RequestBody @Valid InventoryRequestDTO inventoryRequest) {
        return ResponseEntity
                .ok(inventoryService.addInventoryStock(productId, inventoryRequest));
    }

    @PostMapping("/check")
    public ResponseEntity<InventoryResponseDTO> checkStock(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody StockReservationDTO stockReservation) {
        return ResponseEntity.ok(inventoryService.checkStock(stockReservation));
    }

    @PostMapping("/reserve")
    public ResponseEntity<StockReservationDTO> reserveStock(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid StockReservationDTO stockReservation) {
        return ResponseEntity.ok(inventoryService.reserveStock(stockReservation));
    }

    @PostMapping("/release")
    public ResponseEntity<StockReservationDTO> releaseStock(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid StockReservationDTO stockReservation) {
        return ResponseEntity.ok(inventoryService.releaseStock(stockReservation));
    }

    @PostMapping("/confirm")
    public ResponseEntity<StockReservationDTO> confirmStock(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody StockReservationDTO stockReservation) {
        return ResponseEntity.ok(inventoryService.confirmStock(stockReservation));
    }
}
