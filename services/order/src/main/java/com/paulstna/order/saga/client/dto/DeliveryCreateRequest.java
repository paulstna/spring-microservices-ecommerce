package com.paulstna.order.saga.client.dto;

import java.util.UUID;

public record DeliveryCreateRequest(
        UUID orderId, UUID userId, String recipientName, String street, String city,
        String state, String postalCode, String country, String carrier) {
}