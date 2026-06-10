package com.paulstna.user.service;

import com.paulstna.user.dto.request.AddressRequestDTO;
import com.paulstna.user.dto.response.AddressResponseDTO;
import com.paulstna.user.exception.ResourceNotFoundException;
import com.paulstna.user.mapper.AddressMapper;
import com.paulstna.user.model.Address;
import com.paulstna.user.model.UserProfile;
import com.paulstna.user.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements IAddressService {

    private final AddressRepository addressRepository;
    private final IUserProfileService userProfileService;

    @Override
    public List<AddressResponseDTO> getUserAddresses(UUID userId) {
        return addressRepository.findByUserProfileId(userId).stream()
                .map(AddressMapper::toAddressResponse)
                .toList();
    }

    @Override
    public AddressResponseDTO getUserAddress(UUID userId, UUID addressId) {
        Address address = findAddressOrThrow(userId, addressId);
        return AddressMapper.toAddressResponse(address);
    }

    @Override
    public AddressResponseDTO createAddress(UUID userId, AddressRequestDTO createAddressRequest) {
        Address address = AddressMapper.requestToEntity(
                createAddressRequest,
                new Address()
        );
        UserProfile userProfile = userProfileService.getUserProfileEntity(userId);
        address.setUserProfile(userProfile);
        return AddressMapper.toAddressResponse(addressRepository.save(address));
    }

    @Override
    public AddressResponseDTO updateAddress(UUID userId, UUID addressId, AddressRequestDTO createAddressRequest) {
        Address address = findAddressOrThrow(userId, addressId);
        address = AddressMapper.requestToEntity(createAddressRequest, address);
        return AddressMapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    @Override
    public AddressResponseDTO setDefaultAddress(UUID userId, UUID addressId) {
        Address newDefault = findAddressOrThrow(userId, addressId);

        addressRepository.findByUserProfileIdAndIsDefaultTrue(userId)
                .ifPresent(current -> {
                    if (!current.getId().equals(addressId)) {
                        current.setDefault(false);
                        addressRepository.save(current);
                    }
                });

        newDefault.setDefault(true);
        return AddressMapper.toAddressResponse(addressRepository.save(newDefault)
        );
    }

    @Override
    public void deleteAddress(UUID userId, UUID addressId) {
        Address address = findAddressOrThrow(userId, addressId);
        addressRepository.delete(address);
    }

    private Address findAddressOrThrow(UUID userId, UUID addressId) {
        return addressRepository.findByUserProfileIdAndId(userId, addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Address " + addressId + " not found"
                        ));
    }
}
