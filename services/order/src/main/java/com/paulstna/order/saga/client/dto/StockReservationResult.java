package com.paulstna.order.saga.client.dto;

public record StockReservationResult(
        StockStatus status, String message) {
}