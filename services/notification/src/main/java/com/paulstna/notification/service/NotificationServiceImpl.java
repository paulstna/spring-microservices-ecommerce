package com.paulstna.notification.service;

import com.paulstna.notification.dto.NotificationResponseDTO;
import com.paulstna.notification.event.OrderConfirmedEvent;
import com.paulstna.notification.event.OrderFailedEvent;
import com.paulstna.notification.mapper.NotificationMapper;
import com.paulstna.notification.model.Notification;
import com.paulstna.notification.model.NotificationStatus;
import com.paulstna.notification.model.NotificationType;
import com.paulstna.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        if (alreadyProcessed(event.orderId(), NotificationType.ORDER_CONFIRMED)) return;

        String message = "Your order " + event.orderId()
                + " has been confirmed. Total amount: " + event.totalAmount();

        deliverAndPersist(event.orderId(), event.userId(), NotificationType.ORDER_CONFIRMED, message);
    }

    @Override
    @Transactional
    public void handleOrderFailed(OrderFailedEvent event) {
        if (alreadyProcessed(event.orderId(), NotificationType.ORDER_FAILED)) return;

        String message = "Your order " + event.orderId()
                + " could not be completed. Reason: " + event.reason();

        deliverAndPersist(event.orderId(), event.userId(), NotificationType.ORDER_FAILED, message);
    }

    @Override
    public List<NotificationResponseDTO> getByUserId(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    private boolean alreadyProcessed(UUID orderId, NotificationType type) {
        if (notificationRepository.existsByOrderIdAndType(orderId, type)) {
            log.info("Notification {} for order {} already processed, skipping", type, orderId);
            return true;
        }
        return false;
    }

    private void deliverAndPersist(UUID orderId, UUID userId, NotificationType type, String message) {
        log.info("[EMAIL -> user {}] {}", userId, message);

        Notification notification = new Notification();
        notification.setOrderId(orderId);
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setStatus(NotificationStatus.SENT);
        notificationRepository.save(notification);
    }
}