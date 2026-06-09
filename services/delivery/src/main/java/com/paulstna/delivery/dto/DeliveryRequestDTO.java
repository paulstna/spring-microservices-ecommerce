package com.paulstna.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequestDTO {

    @NotNull(message = "orderId must not be null")
    private UUID orderId;

    @NotNull(message = "userId must not be null")
    private UUID userId;

    @NotBlank(message = "recipientName must not be blank")
    private String recipientName;

    @NotBlank(message = "street must not be blank")
    private String street;

    @NotBlank(message = "city must not be blank")
    private String city;

    private String state;

    @NotBlank(message = "postalCode must not be blank")
    private String postalCode;

    @NotBlank(message = "country must not be blank")
    private String country;

    private String carrier;
}