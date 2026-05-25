package com.paulstna.inventory.product.repository;

import com.paulstna.inventory.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByActiveTrue();

    List<Product> findAllByCategoryAndActiveTrue(String categoryName);

    Optional<Product> findByIdAndActiveTrue(UUID productId);
}
