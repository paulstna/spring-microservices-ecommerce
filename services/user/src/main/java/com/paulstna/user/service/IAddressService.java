package com.paulstna.user.service;

import com.paulstna.user.dto.request.CreateAddressRequest;
import com.paulstna.user.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface IAddressService {
    List<AddressResponse> getUserAddresses(UUID userId);

    AddressResponse getUserAddress(UUID userId, UUID addressId);

    AddressResponse createAddress(UUID userId, CreateAddressRequest createAddressRequest);

    AddressResponse updateAddress(UUID userId, UUID addressId, CreateAddressRequest createAddressRequest);

    AddressResponse setDefaultAddress(UUID userId, UUID addressId);

    void deleteAddress(UUID userId, UUID addressId);
}
