package com.paulstna.notification.mapper;

import com.paulstna.notification.dto.NotificationResponseDTO;
import com.paulstna.notification.model.Notification;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationMapper {
    public NotificationResponseDTO toResponse(Notification n) {
        return NotificationResponseDTO.builder()
                .id(n.getId()).orderId(n.getOrderId()).userId(n.getUserId())
                .type(n.getType()).message(n.getMessage()).status(n.getStatus())
                .createdAt(n.getCreatedAt()).build();
    }
}