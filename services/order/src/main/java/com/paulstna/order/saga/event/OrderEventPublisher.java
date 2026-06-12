package com.paulstna.order.saga.event;

public interface OrderEventPublisher {
    void publishOrderConfirmed(OrderConfirmedEvent event);
    void publishOrderFailed(OrderFailedEvent event);
}