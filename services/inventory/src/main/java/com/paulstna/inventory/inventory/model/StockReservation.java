package com.paulstna.inventory.inventory.model;

import com.paulstna.inventory.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "stock_reservations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"order_id", "product_id"}))
@Getter
@Setter
public class StockReservation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private StockStatus status;

    @Version
    private Long version;
}