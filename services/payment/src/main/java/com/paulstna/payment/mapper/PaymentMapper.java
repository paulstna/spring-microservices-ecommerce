package com.paulstna.payment.mapper;

import com.paulstna.payment.dto.PaymentOperationDTO;
import com.paulstna.payment.dto.PaymentRequestDTO;
import com.paulstna.payment.dto.PaymentResponseDTO;
import com.paulstna.payment.model.Payment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDTO dto, Payment payment) {
        payment.setOrderId(dto.getOrderId());
        payment.setUserId(dto.getUserId());
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency());
        payment.setPaymentMethod(dto.getPaymentMethod());
        return payment;
    }

    public PaymentResponseDTO toPaymentResponse(Payment payment) {
        return PaymentResponseDTO.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionRef(payment.getTransactionRef())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public PaymentOperationDTO toPaymentOperation(Payment payment) {
        return PaymentOperationDTO.builder()
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionRef(payment.getTransactionRef())
                .build();
    }
}