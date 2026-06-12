package com.paulstna.order.order.dto;

import com.paulstna.order.order.model.OrderFailureReason;
import com.paulstna.order.order.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private UUID orderId;
    private UUID userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String currency;
    private String recipientName;
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String paymentMethod;
    private OrderFailureReason failureReason;
    private List<OrderItemResponseDTO> items;
    private Instant createdAt;
    private Instant updatedAt;
}