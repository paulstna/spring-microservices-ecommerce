package com.paulstna.order.saga.event;

import com.paulstna.order.order.model.OrderFailureReason;

import java.time.Instant;
import java.util.UUID;

public record OrderFailedEvent(
        UUID orderId, UUID userId, OrderFailureReason reason, Instant occurredAt) {
}