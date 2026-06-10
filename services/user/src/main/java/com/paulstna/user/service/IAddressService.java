package com.paulstna.user.service;

import com.paulstna.user.dto.request.AddressRequestDTO;
import com.paulstna.user.dto.response.AddressResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IAddressService {
    List<AddressResponseDTO> getUserAddresses(UUID userId);

    AddressResponseDTO getUserAddress(UUID userId, UUID addressId);

    AddressResponseDTO createAddress(UUID userId, AddressRequestDTO createAddressRequest);

    AddressResponseDTO updateAddress(UUID userId, UUID addressId, AddressRequestDTO createAddressRequest);

    AddressResponseDTO setDefaultAddress(UUID userId, UUID addressId);

    void deleteAddress(UUID userId, UUID addressId);
}
