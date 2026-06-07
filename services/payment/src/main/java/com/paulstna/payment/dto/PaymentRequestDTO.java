package com.paulstna.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull(message = "orderId must not be null")
    private UUID orderId;

    @NotNull(message = "userId must not be null")
    private UUID userId;

    @NotNull(message = "amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "currency must not be null")
    private String currency;

    @NotNull(message = "paymentMethod must not be null")
    private String paymentMethod;
}