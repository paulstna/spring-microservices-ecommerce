package com.paulstna.order.saga.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Placeholder for Kafka. should be replaced later
@Component
@Slf4j
public class LoggingOrderEventPublisher implements OrderEventPublisher {

    @Override
    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        log.info("[EVENT] OrderConfirmed -> {}", event);
    }

    @Override
    public void publishOrderFailed(OrderFailedEvent event) {
        log.info("[EVENT] OrderFailed -> {}", event);
    }
}