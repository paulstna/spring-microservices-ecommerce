package com.paulstna.delivery.service;

import com.paulstna.delivery.dto.DeliveryOperationDTO;
import com.paulstna.delivery.dto.DeliveryRequestDTO;
import com.paulstna.delivery.dto.DeliveryResponseDTO;

import java.util.UUID;

public interface IDeliveryService {

    DeliveryOperationDTO createShipment(DeliveryRequestDTO deliveryRequest);

    DeliveryOperationDTO cancelShipment(DeliveryOperationDTO operation);

    DeliveryResponseDTO getShipmentByOrderId(UUID orderId);
}