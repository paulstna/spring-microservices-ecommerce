package com.paulstna.delivery.model;

import com.paulstna.delivery.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
public class Delivery extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
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

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private Instant estimatedDeliveryDate;

    @Version
    private Long version;
}
