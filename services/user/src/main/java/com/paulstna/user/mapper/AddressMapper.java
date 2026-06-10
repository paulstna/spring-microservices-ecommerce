package com.paulstna.user.mapper;

import com.paulstna.user.dto.request.AddressRequestDTO;
import com.paulstna.user.dto.response.AddressResponseDTO;
import com.paulstna.user.model.Address;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AddressMapper {

    public AddressResponseDTO toAddressResponse(Address address) {
        return AddressResponseDTO
                .builder()
                .id(address.getId())
                .alias(address.getAlias())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .countryCode(address.getCountryCode())
                .instructions(address.getInstructions())
                .isDefault(address.isDefault())
                .build();
    }

    public Address requestToEntity(AddressRequestDTO createAddressRequest, Address address) {
        address.setCity(createAddressRequest.getCity());
        address.setState(createAddressRequest.getState());
        address.setAlias(createAddressRequest.getAlias());
        address.setCountryCode(createAddressRequest.getCountryCode());
        address.setPostalCode(createAddressRequest.getPostalCode());
        address.setStreet(createAddressRequest.getStreet());
        address.setInstructions(createAddressRequest.getInstructions());
        address.setDefault(createAddressRequest.isDefault());
        return address;
    }
}
