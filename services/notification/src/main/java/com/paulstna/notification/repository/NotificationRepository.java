package com.paulstna.notification.repository;

import com.paulstna.notification.model.Notification;
import com.paulstna.notification.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    boolean existsByOrderIdAndType(UUID orderId, NotificationType type);

    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
