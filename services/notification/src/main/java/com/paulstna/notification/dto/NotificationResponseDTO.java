package com.paulstna.notification.dto;

import com.paulstna.notification.model.NotificationStatus;
import com.paulstna.notification.model.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class NotificationResponseDTO {
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private NotificationType type;
    private String message;
    private NotificationStatus status;
    private Instant createdAt;
}