package com.paulstna.payment.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageConstants {

    public final String PAYMENT_AUTHORIZED = "Payment authorized successfully";
    public final String PAYMENT_DECLINED = "Payment declined by the provider";
    public final String PAYMENT_CAPTURED = "Payment captured successfully";
    public final String PAYMENT_REFUNDED = "Payment refunded successfully";
    public final String PAYMENT_ALREADY_EXISTS = "A payment already exists for this order";
}
