package com.paulstna.payment.service;

import com.paulstna.payment.exception.ResourceNotFoundException;
import com.paulstna.payment.constants.MessageConstants;
import com.paulstna.payment.dto.PaymentOperationDTO;
import com.paulstna.payment.dto.PaymentRequestDTO;
import com.paulstna.payment.dto.PaymentResponseDTO;
import com.paulstna.payment.model.Payment;
import com.paulstna.payment.model.PaymentStatus;
import com.paulstna.payment.repository.PaymentRepository;
import com.paulstna.payment.payment.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID orderId;
    private UUID userId;
    private Payment payment;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();

        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setOrderId(orderId);
        payment.setUserId(userId);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setCurrency("USD");
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setStatus(PaymentStatus.AUTHORIZED);
        payment.setTransactionRef("TXN-123");
    }

    private PaymentRequestDTO buildRequest(BigDecimal amount) {
        return PaymentRequestDTO.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .currency("USD")
                .paymentMethod("CREDIT_CARD")
                .build();
    }

    // ─── authorizePayment ──────────────────────────────────────────────────────

    @Test
    void authorizePayment_authorizes_whenAmountIsWithinLimit() {
        PaymentRequestDTO request = buildRequest(new BigDecimal("250.00"));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentOperationDTO result = paymentService.authorizePayment(request);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.PAYMENT_AUTHORIZED);
        assertThat(result.getTransactionRef()).isNotNull();
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void authorizePayment_isDeclined_whenAmountExceedsLimit() {
        PaymentRequestDTO request = buildRequest(new BigDecimal("99999999.00"));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentOperationDTO result = paymentService.authorizePayment(request);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.PAYMENT_DECLINED);
        assertThat(result.getTransactionRef()).isNull();
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void authorizePayment_isIdempotent_whenPaymentAlreadyExists() {
        PaymentRequestDTO request = buildRequest(new BigDecimal("250.00"));
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        PaymentOperationDTO result = paymentService.authorizePayment(request);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        verify(paymentRepository, never()).save(any()); // no doble cobro
    }

    // ─── capturePayment ────────────────────────────────────────────────────────

    @Test
    void capturePayment_capturesCorrectly_whenAuthorized() {
        payment.setStatus(PaymentStatus.AUTHORIZED);
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentOperationDTO result = paymentService.capturePayment(operation);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CAPTURED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.PAYMENT_CAPTURED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CAPTURED);
        verify(paymentRepository).save(payment);
    }

    @Test
    void capturePayment_isIdempotent_whenAlreadyCaptured() {
        payment.setStatus(PaymentStatus.CAPTURED);
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        PaymentOperationDTO result = paymentService.capturePayment(operation);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CAPTURED);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void capturePayment_throwsIllegalState_whenNotAuthorized() {
        payment.setStatus(PaymentStatus.FAILED);
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.capturePayment(operation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot capture");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void capturePayment_throwsResourceNotFound_whenPaymentMissing() {
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.capturePayment(operation))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── refundPayment ─────────────────────────────────────────────────────────

    @Test
    void refundPayment_refundsCorrectly_whenCaptured() {
        payment.setStatus(PaymentStatus.CAPTURED);
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentOperationDTO result = paymentService.refundPayment(operation);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.PAYMENT_REFUNDED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(paymentRepository).save(payment);
    }

    @Test
    void refundPayment_refundsCorrectly_whenOnlyAuthorized() {
        payment.setStatus(PaymentStatus.AUTHORIZED);
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        PaymentOperationDTO result = paymentService.refundPayment(operation);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(paymentRepository).save(payment);
    }

    @Test
    void refundPayment_isIdempotent_whenAlreadyRefunded() {
        payment.setStatus(PaymentStatus.REFUNDED);
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        PaymentOperationDTO result = paymentService.refundPayment(operation);

        assertThat(result.getStatus()).isEqualTo(PaymentStatus.REFUNDED);
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void refundPayment_throwsIllegalState_whenPaymentFailed() {
        payment.setStatus(PaymentStatus.FAILED);
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.refundPayment(operation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot refund");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void refundPayment_throwsResourceNotFound_whenPaymentMissing() {
        PaymentOperationDTO operation = PaymentOperationDTO.builder().orderId(orderId).build();
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.refundPayment(operation))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── getPaymentByOrderId ───────────────────────────────────────────────────

    @Test
    void getPaymentByOrderId_returnsPaymentResponse() {
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        PaymentResponseDTO result = paymentService.getPaymentByOrderId(orderId);

        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getAmount()).isEqualByComparingTo("250.00");
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
    }

    @Test
    void getPaymentByOrderId_throwsResourceNotFound_whenMissing() {
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(orderId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}