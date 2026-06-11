package com.paulstna.order.model;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    PAYMENT_CHARGED,
    CONFIRMED,
    FAILED,
    CANCELLED
}
