package com.paulstna.user.mapper;

import com.paulstna.user.dto.request.CreateAddressRequest;
import com.paulstna.user.dto.response.AddressResponse;
import com.paulstna.user.model.Address;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AddressMapper {

    public AddressResponse toAddressResponse(Address address) {
        return AddressResponse
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

    public Address requestToEntity(CreateAddressRequest createAddressRequest, Address address) {
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
