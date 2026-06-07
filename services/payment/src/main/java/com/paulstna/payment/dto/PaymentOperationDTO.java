package com.paulstna.payment.dto;

import com.paulstna.payment.model.PaymentStatus;
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
public class PaymentOperationDTO {

    @NotNull(message = "orderId must not be null")
    private UUID orderId;

    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionRef;
    private String message;
}