package com.paulstna.payment.controller;

import com.paulstna.payment.dto.PaymentOperationDTO;
import com.paulstna.payment.dto.PaymentRequestDTO;
import com.paulstna.payment.dto.PaymentResponseDTO;
import com.paulstna.payment.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/{version}/payments", version = "v1")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentByOrderId(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }

    @PostMapping("/authorize")
    public ResponseEntity<PaymentOperationDTO> authorizePayment(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid PaymentRequestDTO paymentRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.authorizePayment(paymentRequest));
    }

    @PostMapping("/capture")
    public ResponseEntity<PaymentOperationDTO> capturePayment(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid PaymentOperationDTO operation) {
        return ResponseEntity.ok(paymentService.capturePayment(operation));
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentOperationDTO> refundPayment(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestBody @Valid PaymentOperationDTO operation) {
        return ResponseEntity.ok(paymentService.refundPayment(operation));
    }
}