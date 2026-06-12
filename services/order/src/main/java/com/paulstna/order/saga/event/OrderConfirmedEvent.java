package com.paulstna.order.saga.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID orderId, UUID userId, BigDecimal totalAmount, Instant occurredAt) {
}