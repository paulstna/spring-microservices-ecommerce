package com.paulstna.order.saga.event;

import com.paulstna.order.saga.constants.SagaConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void publishOrderConfirmed(OrderConfirmedEvent event) {
        send(SagaConstants.ORDER_CONFIRMED_TOPIC, event.orderId().toString(), event);
    }

    @Override
    public void publishOrderFailed(OrderFailedEvent event) {
        send(SagaConstants.ORDER_FAILED_TOPIC, event.orderId().toString(), event);
    }

    private void send(String topic, String key, Object event) {
        try {
            kafkaTemplate.send(topic, key, event).whenComplete((result, exception) -> {
                if (exception != null) {
                    logFailedToPublishError(topic, exception.getMessage());
                } else {
                    log.info("Event published to {} [partition={}, offset={}]", topic,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                }
            });
        } catch (Exception exception) {
            logFailedToPublishError(topic, exception.getMessage());
        }
    }

    private void logFailedToPublishError(String topic, String errorMessage) {
        log.error("Failed to publish event to {}: {}", topic, errorMessage);
    }
}
