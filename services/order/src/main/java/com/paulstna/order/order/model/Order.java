package com.paulstna.order.order.model;

import com.paulstna.order.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private String currency;

    // Snapshot for Delivery
    private String recipientName;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    @Enumerated(EnumType.STRING)
    private OrderFailureReason failureReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private String paymentMethod;

    @Version
    private Long version;
}