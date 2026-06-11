package com.paulstna.order.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageConstants {

    public final String ORDER_NOT_FOUND = "Order not found: ";
    public final String INVALID_TRANSITION = "Invalid status transition: ";
    public final String CANNOT_CANCEL = "Cannot cancel an order in status ";
}