package com.paulstna.payment.service;

import com.paulstna.payment.dto.PaymentOperationDTO;
import com.paulstna.payment.dto.PaymentRequestDTO;
import com.paulstna.payment.dto.PaymentResponseDTO;

import java.util.UUID;

public interface IPaymentService {
    PaymentOperationDTO authorizePayment(PaymentRequestDTO paymentRequest);

    PaymentOperationDTO capturePayment(PaymentOperationDTO operation);

    PaymentOperationDTO refundPayment(PaymentOperationDTO operation);

    PaymentResponseDTO getPaymentByOrderId(UUID orderId);
}
