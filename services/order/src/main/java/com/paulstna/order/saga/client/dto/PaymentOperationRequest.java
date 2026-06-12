package com.paulstna.order.saga.client.dto;

import java.util.UUID;

public record PaymentOperationRequest(UUID orderId) {
}