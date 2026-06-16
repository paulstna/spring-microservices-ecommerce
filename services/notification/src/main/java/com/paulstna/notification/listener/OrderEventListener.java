package com.paulstna.notification.listener;

import com.paulstna.notification.constants.NotificationTopics;
import com.paulstna.notification.event.OrderConfirmedEvent;
import com.paulstna.notification.event.OrderFailedEvent;
import com.paulstna.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final INotificationService notificationService;

    @KafkaListener(topics = NotificationTopics.ORDER_CONFIRMED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Received order-confirmed event for order {}", event.orderId());
        notificationService.handleOrderConfirmed(event);
    }

    @KafkaListener(topics = NotificationTopics.ORDER_FAILED,
            groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderFailed(OrderFailedEvent event) {
        log.info("Received order-failed event for order {}", event.orderId());
        notificationService.handleOrderFailed(event);
    }
}