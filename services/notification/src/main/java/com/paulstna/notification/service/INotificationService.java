package com.paulstna.notification.service;

import com.paulstna.notification.dto.NotificationResponseDTO;
import com.paulstna.notification.event.OrderConfirmedEvent;
import com.paulstna.notification.event.OrderFailedEvent;

import java.util.List;
import java.util.UUID;

public interface INotificationService {
    void handleOrderConfirmed(OrderConfirmedEvent event);

    void handleOrderFailed(OrderFailedEvent event);

    List<NotificationResponseDTO> getByUserId(UUID userId);
}
