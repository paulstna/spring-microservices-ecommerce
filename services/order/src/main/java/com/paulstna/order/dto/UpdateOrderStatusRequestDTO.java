package com.paulstna.order.dto;

import com.paulstna.order.model.OrderFailureReason;
import com.paulstna.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequestDTO {

    @NotNull(message = "status must not be null")
    private OrderStatus status;

    private OrderFailureReason failureReason;
}