package com.paulstna.order.order.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class OrderItemRequestDTO {

    @NotNull(message = "productId must not be null")
    private UUID productId;

    @NotBlank(message = "productName must not be blank")
    private String productName;

    @NotNull(message = "quantity must not be null")
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "unitPrice must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "unitPrice must be greater than 0")
    private BigDecimal unitPrice;
}