package com.paulstna.user.controller;

import com.paulstna.user.dto.request.CreateAddressRequest;
import com.paulstna.user.dto.response.AddressResponse;
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
    public ResponseEntity<List<AddressResponse>> getUserAddresses(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId) {

        return ResponseEntity.ok().body(addressService.getUserAddresses(userId));
    }

    @GetMapping(path = "/{addressId}")
    public ResponseEntity<AddressResponse> getUserAddress(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID addressId) {

        return ResponseEntity.ok().body(addressService.getUserAddress(userId, addressId));
    }

    @PostMapping
    public ResponseEntity<AddressResponse> createUserAddress(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @RequestBody @Valid CreateAddressRequest createAddressRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(userId, createAddressRequest));
    }

    @PutMapping(path = "/{addressId}")
    public ResponseEntity<AddressResponse> updateUserAddress(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @PathVariable UUID addressId,
            @RequestBody @Valid CreateAddressRequest createAddressRequest) {

        return ResponseEntity.ok().body(addressService.updateAddress(userId, addressId, createAddressRequest));
    }

    @PutMapping(path = "/{addressId}/default")
    public ResponseEntity<AddressResponse> updateUserAddressDefault(
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
