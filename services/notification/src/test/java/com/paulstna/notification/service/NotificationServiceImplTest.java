package com.paulstna.notification.service;

import com.paulstna.notification.event.OrderConfirmedEvent;
import com.paulstna.notification.model.Notification;
import com.paulstna.notification.model.NotificationStatus;
import com.paulstna.notification.model.NotificationType;
import com.paulstna.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @InjectMocks
    private NotificationServiceImpl service;

    @Test
    void handleOrderConfirmed_persistsSentNotification_whenNotProcessed() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OrderConfirmedEvent event = new OrderConfirmedEvent(orderId, userId, new BigDecimal("20.00"), Instant.now());
        when(notificationRepository.existsByOrderIdAndType(orderId, NotificationType.ORDER_CONFIRMED))
                .thenReturn(false);

        service.handleOrderConfirmed(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(orderId);
        assertThat(saved.getType()).isEqualTo(NotificationType.ORDER_CONFIRMED);
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void handleOrderConfirmed_skips_whenAlreadyProcessed() {
        UUID orderId = UUID.randomUUID();
        OrderConfirmedEvent event = new OrderConfirmedEvent(orderId, UUID.randomUUID(), BigDecimal.TEN, Instant.now());
        when(notificationRepository.existsByOrderIdAndType(orderId, NotificationType.ORDER_CONFIRMED))
                .thenReturn(true);

        service.handleOrderConfirmed(event);

        verify(notificationRepository, never()).save(any());
    }
}