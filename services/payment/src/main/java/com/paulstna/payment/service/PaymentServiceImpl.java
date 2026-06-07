package com.paulstna.payment.service;

import com.paulstna.payment.exception.ResourceNotFoundException;
import com.paulstna.payment.constants.MessageConstants;
import com.paulstna.payment.dto.PaymentOperationDTO;
import com.paulstna.payment.dto.PaymentRequestDTO;
import com.paulstna.payment.dto.PaymentResponseDTO;
import com.paulstna.payment.mapper.PaymentMapper;
import com.paulstna.payment.model.Payment;
import com.paulstna.payment.model.PaymentStatus;
import com.paulstna.payment.repository.PaymentRepository;
import com.paulstna.payment.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    //Regla de la pasarela simulada: cualquier monto por encima de este límite
    private static final BigDecimal AUTHORIZATION_LIMIT = new BigDecimal("999999.00");

    private final PaymentRepository paymentRepository;

    @Transactional
    @Override
    public PaymentOperationDTO authorizePayment(PaymentRequestDTO paymentRequest) {
        // Idempotencia: una orden nunca debe cobrarse dos veces.
        return paymentRepository.findByOrderId(paymentRequest.getOrderId())
                .map(this::toAlreadyProcessedOperation)
                .orElseGet(() -> createAndAuthorize(paymentRequest));
    }

    @Transactional
    @Override
    public PaymentOperationDTO capturePayment(PaymentOperationDTO operation) {
        Payment payment = getPaymentByOrderIdOrThrow(operation.getOrderId());

        if (payment.getStatus() == PaymentStatus.CAPTURED) {           // idempotente
            return buildOperation(payment, MessageConstants.PAYMENT_CAPTURED);
        }
        if (payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new IllegalStateException(
                    "Cannot capture a payment in status " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.CAPTURED);
        paymentRepository.save(payment);
        return buildOperation(payment, MessageConstants.PAYMENT_CAPTURED);
    }

    @Transactional
    @Override
    public PaymentOperationDTO refundPayment(PaymentOperationDTO operation) {
        Payment payment = getPaymentByOrderIdOrThrow(operation.getOrderId());

        if (payment.getStatus() == PaymentStatus.REFUNDED) {           // idempotente
            return buildOperation(payment, MessageConstants.PAYMENT_REFUNDED);
        }
        if (payment.getStatus() != PaymentStatus.AUTHORIZED
                && payment.getStatus() != PaymentStatus.CAPTURED) {
            throw new IllegalStateException(
                    "Cannot refund a payment in status " + payment.getStatus());
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        return buildOperation(payment, MessageConstants.PAYMENT_REFUNDED);
    }

    @Override
    public PaymentResponseDTO getPaymentByOrderId(UUID orderId) {
        return PaymentMapper.toPaymentResponse(getPaymentByOrderIdOrThrow(orderId));
    }

    // ─── helpers ────────────────────────────────────────────────────────────

    private PaymentOperationDTO createAndAuthorize(PaymentRequestDTO request) {
        Payment payment = PaymentMapper.toEntity(request, new Payment());
        payment.setStatus(PaymentStatus.PENDING);

        boolean approved = request.getAmount().compareTo(AUTHORIZATION_LIMIT) <= 0;
        if (approved) {
            payment.setStatus(PaymentStatus.AUTHORIZED);
            payment.setTransactionRef("TXN-" + UUID.randomUUID());
            paymentRepository.save(payment);
            return buildOperation(payment, MessageConstants.PAYMENT_AUTHORIZED);
        }

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        return buildOperation(payment, MessageConstants.PAYMENT_DECLINED);
    }

    private PaymentOperationDTO toAlreadyProcessedOperation(Payment payment) {
        String message = payment.getStatus() == PaymentStatus.AUTHORIZED
                ? MessageConstants.PAYMENT_AUTHORIZED
                : MessageConstants.PAYMENT_ALREADY_EXISTS;
        return buildOperation(payment, message);
    }

    private PaymentOperationDTO buildOperation(Payment payment, String message) {
        PaymentOperationDTO operation = PaymentMapper.toPaymentOperation(payment);
        operation.setMessage(message);
        return operation;
    }

    private Payment getPaymentByOrderIdOrThrow(UUID orderId) {
        return paymentRepository.findByOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException("No payment found for order id: " + orderId));
    }
}