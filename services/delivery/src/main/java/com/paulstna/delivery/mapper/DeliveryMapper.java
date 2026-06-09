package com.paulstna.delivery.mapper;

import com.paulstna.delivery.dto.DeliveryOperationDTO;
import com.paulstna.delivery.dto.DeliveryRequestDTO;
import com.paulstna.delivery.dto.DeliveryResponseDTO;
import com.paulstna.delivery.model.Delivery;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DeliveryMapper {

    public Delivery toEntity(DeliveryRequestDTO dto, Delivery delivery) {
        delivery.setOrderId(dto.getOrderId());
        delivery.setUserId(dto.getUserId());
        delivery.setRecipientName(dto.getRecipientName());
        delivery.setStreet(dto.getStreet());
        delivery.setCity(dto.getCity());
        delivery.setState(dto.getState());
        delivery.setPostalCode(dto.getPostalCode());
        delivery.setCountry(dto.getCountry());
        delivery.setCarrier(dto.getCarrier());
        return delivery;
    }

    public DeliveryResponseDTO toDeliveryResponse(Delivery delivery) {
        return DeliveryResponseDTO.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrderId())
                .userId(delivery.getUserId())
                .recipientName(delivery.getRecipientName())
                .street(delivery.getStreet())
                .city(delivery.getCity())
                .state(delivery.getState())
                .postalCode(delivery.getPostalCode())
                .country(delivery.getCountry())
                .carrier(delivery.getCarrier())
                .trackingNumber(delivery.getTrackingNumber())
                .status(delivery.getStatus())
                .estimatedDeliveryDate(delivery.getEstimatedDeliveryDate())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }

    public DeliveryOperationDTO toDeliveryOperation(Delivery delivery) {
        return DeliveryOperationDTO.builder()
                .orderId(delivery.getOrderId())
                .status(delivery.getStatus())
                .trackingNumber(delivery.getTrackingNumber())
                .build();
    }
}