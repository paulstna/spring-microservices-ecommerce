package com.paulstna.user.repository;

import com.paulstna.user.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserProfileId(UUID id);

    Optional<Address> findByUserProfileIdAndId(UUID id, UUID addressId);

    Optional<Address> findByUserProfileIdAndIsDefaultTrue(UUID userId);
}
