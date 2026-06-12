package com.paulstna.order.saga.client;

import com.paulstna.order.saga.constants.SagaConstants;
import com.paulstna.order.saga.client.dto.PaymentAuthorizeRequest;
import com.paulstna.order.saga.client.dto.PaymentOperationRequest;
import com.paulstna.order.saga.client.dto.PaymentOperationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment", path = "/api/v1/payments")
public interface PaymentClient {

    @PostMapping("/authorize")
    PaymentOperationResult authorize(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody PaymentAuthorizeRequest request);

    @PostMapping("/capture")
    void capture(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody PaymentOperationRequest request);

    @PostMapping("/refund")
    void refund(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody PaymentOperationRequest request);
}