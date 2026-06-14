package com.paulstna.order.saga.event;

import com.paulstna.order.order.model.OrderFailureReason;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaOrderEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaOrderEventPublisher publisher;

    @Test
    void publishOrderConfirmed_sendsToConfirmedTopic_withOrderIdAsKey() {
        UUID orderId = UUID.randomUUID();
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                orderId, UUID.randomUUID(), new BigDecimal("20.00"), Instant.now());
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(new CompletableFuture<SendResult<String, Object>>());

        publisher.publishOrderConfirmed(event);

        verify(kafkaTemplate).send(eq("order-confirmed"), eq(orderId.toString()), eq(event));
    }

    @Test
    void publishOrderFailed_sendsToFailedTopic_withOrderIdAsKey() {
        UUID orderId = UUID.randomUUID();
        OrderFailedEvent event = new OrderFailedEvent(
                orderId, UUID.randomUUID(), OrderFailureReason.PAYMENT_FAILED, Instant.now());
        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(new CompletableFuture<SendResult<String, Object>>());

        publisher.publishOrderFailed(event);

        verify(kafkaTemplate).send(eq("order-failed"), eq(orderId.toString()), eq(event));
    }
}