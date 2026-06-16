package com.paulstna.notification.event;

import java.time.Instant;
import java.util.UUID;

public record OrderFailedEvent(
        UUID orderId, UUID userId,
        String reason, Instant occurredAt) {
}