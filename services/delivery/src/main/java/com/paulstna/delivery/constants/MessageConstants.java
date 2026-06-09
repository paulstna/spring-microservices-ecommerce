package com.paulstna.delivery.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageConstants {

    public final String SHIPMENT_CREATED = "Shipment created successfully";
    public final String SHIPMENT_FAILED = "Shipment creation failed: destination not serviceable";
    public final String SHIPMENT_CANCELLED = "Shipment cancelled successfully";
    public final String SHIPMENT_ALREADY_EXISTS = "A shipment already exists for this order";
}