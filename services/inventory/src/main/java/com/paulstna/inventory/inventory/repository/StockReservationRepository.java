package com.paulstna.inventory.inventory.repository;

import com.paulstna.inventory.inventory.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID> {
    Optional<StockReservation> findByOrderIdAndProductId(UUID orderId, UUID productId);
}
