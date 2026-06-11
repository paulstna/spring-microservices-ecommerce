package com.paulstna.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequestDTO {

    @NotNull(message = "userId must not be null")
    private UUID userId;

    @NotBlank(message = "currency must not be blank")
    private String currency;

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

    @NotEmpty(message = "items must not be empty")
    @Valid
    private List<OrderItemRequestDTO> items;
}