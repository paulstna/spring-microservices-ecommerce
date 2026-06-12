package com.paulstna.order.saga.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentAuthorizeRequest(
        UUID orderId, UUID userId, BigDecimal amount, String currency, String paymentMethod) {
}