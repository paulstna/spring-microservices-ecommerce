package com.paulstna.delivery.dto;

import com.paulstna.delivery.model.DeliveryStatus;
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
public class DeliveryOperationDTO {

    @NotNull(message = "orderId must not be null")
    private UUID orderId;

    private DeliveryStatus status;
    private String trackingNumber;
    private String message;
}