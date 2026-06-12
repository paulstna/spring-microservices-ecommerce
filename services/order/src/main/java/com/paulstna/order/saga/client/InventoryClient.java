package com.paulstna.order.saga.client;

import com.paulstna.order.saga.constants.SagaConstants;
import com.paulstna.order.saga.client.dto.StockReservationRequest;
import com.paulstna.order.saga.client.dto.StockReservationResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "inventory", path = "/api/v1/inventory")
public interface InventoryClient {

    @PostMapping("/reserve")
    StockReservationResult reserve(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody StockReservationRequest request);

    @PostMapping("/release")
    void release(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody StockReservationRequest request);

    @PostMapping("/confirm")
    void confirm(
            @RequestHeader(SagaConstants.CORRELATION_ID_HEADER) String correlationId,
            @RequestBody StockReservationRequest request);
}