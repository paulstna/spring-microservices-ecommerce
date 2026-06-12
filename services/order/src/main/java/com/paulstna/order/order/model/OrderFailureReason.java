package com.paulstna.order.order.model;

public enum OrderFailureReason {
    OUT_OF_STOCK,
    PAYMENT_FAILED,
    DELIVERY_FAILED,
    SAGA_ERROR
}
