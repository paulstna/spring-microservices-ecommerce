package com.paulstna.delivery.dto;

import com.paulstna.delivery.model.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponseDTO {

    private UUID deliveryId;
    private UUID orderId;
    private UUID userId;
    private String recipientName;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String carrier;
    private String trackingNumber;
    private DeliveryStatus status;
    private Instant estimatedDeliveryDate;
    private Instant updatedAt;
}