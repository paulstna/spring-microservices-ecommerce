package com.paulstna.order.saga.client.dto;

public record PaymentOperationResult(
        PaymentStatus status, String transactionRef, String message) {
}