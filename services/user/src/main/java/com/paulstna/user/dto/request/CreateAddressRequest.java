package com.paulstna.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequest {

    @NotBlank
    @Size(max = 40)
    private String alias;

    @NotBlank
    @Size(max = 200)
    private String street;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String postalCode;

    @NotBlank
    @Size(min = 2, max = 2)
    private String countryCode;

    @Size(max = 500)
    private String instructions;

    private boolean isDefault = false;
}