package com.paulstna.order.saga.config;

import com.paulstna.order.saga.constants.SagaConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name(SagaConstants.ORDER_CONFIRMED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderFailedTopic() {
        return TopicBuilder.name(SagaConstants.ORDER_FAILED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
