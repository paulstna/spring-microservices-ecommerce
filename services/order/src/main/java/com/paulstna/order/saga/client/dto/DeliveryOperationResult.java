package com.paulstna.order.saga.client.dto;

public record DeliveryOperationResult(
        DeliveryStatus status, String trackingNumber, String message) {
}