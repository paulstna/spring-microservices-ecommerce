package com.paulstna.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    private UUID id;
    private String alias;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode;
    private String instructions;
    private boolean isDefault;
}