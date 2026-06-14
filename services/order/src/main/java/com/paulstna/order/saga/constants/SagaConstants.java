package com.paulstna.order.saga.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SagaConstants {
    public final String CORRELATION_ID_HEADER = "X-paulstna-Correlation-ID";
    public final String ORDER_CONFIRMED_TOPIC = "order-confirmed";
    public final String ORDER_FAILED_TOPIC = "order-failed";
}