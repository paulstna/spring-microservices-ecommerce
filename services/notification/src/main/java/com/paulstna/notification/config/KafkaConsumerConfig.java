package com.paulstna.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JacksonJsonMessageConverter;

@Configuration
public class KafkaConsumerConfig {

    // The payload is received as a byte[] (via ByteArrayDeserializer). This converter
    // deserializes it into the target parameter type expected by each @KafkaListener.
    // Since it is the sole RecordMessageConverter bean in the application context,
    // Spring Boot automatically detects and wires it into the listener container factory.
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}