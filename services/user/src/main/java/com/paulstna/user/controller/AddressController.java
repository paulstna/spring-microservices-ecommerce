package com.paulstna.user.controller;

import com.paulstna.user.dto.request.AddressRequestDTO;
import com.paulstna.user.dto.response.AddressResponseDTO;
import com.paulstna.user.service.IAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/{version}/profiles/me/addresses", version = "v1")
public class AddressController {

    private final IAddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getUserAddresses(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId) {

        return ResponseEntity.ok().body(addressService.getUserAddresses(userId));
    }

    @GetMapping(path = "/{addressId}")
    public ResponseEntity<AddressResponseDTO> getUserAddress(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID addressId) {

        return ResponseEntity.ok().body(addressService.getUserAddress(userId, addressId));
    }

    @PostMapping
    public ResponseEntity<AddressResponseDTO> createUserAddress(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @RequestBody @Valid AddressRequestDTO createAddressRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(userId, createAddressRequest));
    }

    @PutMapping(path = "/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateUserAddress(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID addressId,
            @RequestBody @Valid AddressRequestDTO createAddressRequest) {

        return ResponseEntity.ok().body(addressService.updateAddress(userId, addressId, createAddressRequest));
    }

    @PutMapping(path = "/{addressId}/default")
    public ResponseEntity<AddressResponseDTO> updateUserAddressDefault(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID addressId) {

        return ResponseEntity.ok().body(addressService.setDefaultAddress(userId, addressId));
    }


    @DeleteMapping(path = "/{addressId}")
    public ResponseEntity<Void> deleteUserAddress(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID addressId) {

        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}
