package com.paulstna.order.saga.client;

import com.paulstna.order.saga.constants.SagaConstants;
import com.paulstna.order.saga.client.dto.DeliveryCreateRequest;
import com.paulstna.order.saga.client.dto.DeliveryOperationRequest;
import com.paulstna.order.saga.client.dto.DeliveryOperationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "delivery", path = "/api/v1/deliveries")
public interface DeliveryClient {

    @PostMapping
    DeliveryOperationResult createShipment(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody DeliveryCreateRequest request);

    @PostMapping("/cancel")
    void cancel(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody DeliveryOperationRequest request);
}