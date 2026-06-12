package com.paulstna.order.saga.client.dto;

public enum PaymentStatus {
    PENDING,
    AUTHORIZED,
    CAPTURED,
    FAILED,
    REFUNDED
}
