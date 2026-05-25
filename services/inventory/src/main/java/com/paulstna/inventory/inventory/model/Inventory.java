package com.paulstna.inventory.inventory.model;

import com.paulstna.inventory.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter
@Setter
public class Inventory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID productId;
    private Integer totalStock;
    private Integer reservedStock;
    private Integer availableStock;
}
