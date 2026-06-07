package com.paulstna.payment.model;

import com.paulstna.payment.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    private UUID userId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String paymentMethod;

    private String transactionRef;

    @Version
    private Long version;
}