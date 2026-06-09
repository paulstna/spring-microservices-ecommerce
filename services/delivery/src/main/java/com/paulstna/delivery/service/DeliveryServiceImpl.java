package com.paulstna.delivery.service;

import com.paulstna.delivery.exception.ResourceNotFoundException;
import com.paulstna.delivery.constants.MessageConstants;
import com.paulstna.delivery.dto.DeliveryOperationDTO;
import com.paulstna.delivery.dto.DeliveryRequestDTO;
import com.paulstna.delivery.dto.DeliveryResponseDTO;
import com.paulstna.delivery.mapper.DeliveryMapper;
import com.paulstna.delivery.model.Delivery;
import com.paulstna.delivery.model.DeliveryStatus;
import com.paulstna.delivery.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements IDeliveryService {

    // Simulated carrier coverage: if the destination country is not supported (Delivery FAIL).
    private static final Set<String> SUPPORTED_COUNTRIES = Set.of("US", "EC", "ES", "MX");
    private static final long ESTIMATED_DELIVERY_DAYS = 5;

    private final DeliveryRepository deliveryRepository;

    @Transactional
    @Override
    public DeliveryOperationDTO createShipment(DeliveryRequestDTO deliveryRequest) {
        return deliveryRepository.findByOrderId(deliveryRequest.getOrderId())
                .map(this::toAlreadyProcessedOperation)
                .orElseGet(() -> createAndDispatch(deliveryRequest));
    }

    @Transactional
    @Override
    public DeliveryOperationDTO cancelShipment(DeliveryOperationDTO operation) {
        Delivery delivery = getShipmentByOrderIdOrThrow(operation.getOrderId());

        if (delivery.getStatus() == DeliveryStatus.CANCELLED) {        // idempotente
            return buildOperation(delivery, MessageConstants.SHIPMENT_CANCELLED);
        }
        if (delivery.getStatus() != DeliveryStatus.CREATED) {
            throw new IllegalStateException(
                    "Cannot cancel a shipment in status " + delivery.getStatus());
        }

        delivery.setStatus(DeliveryStatus.CANCELLED);
        deliveryRepository.save(delivery);
        return buildOperation(delivery, MessageConstants.SHIPMENT_CANCELLED);
    }

    @Override
    public DeliveryResponseDTO getShipmentByOrderId(UUID orderId) {
        return DeliveryMapper.toDeliveryResponse(getShipmentByOrderIdOrThrow(orderId));
    }

    private DeliveryOperationDTO createAndDispatch(DeliveryRequestDTO request) {
        Delivery delivery = DeliveryMapper.toEntity(request, new Delivery());
        delivery.setStatus(DeliveryStatus.PENDING);

        boolean serviceable = request.getCountry() != null
                && SUPPORTED_COUNTRIES.contains(request.getCountry().toUpperCase());

        if (serviceable) {
            delivery.setStatus(DeliveryStatus.CREATED);
            delivery.setTrackingNumber("TRK-" + UUID.randomUUID());
            delivery.setEstimatedDeliveryDate(
                    Instant.now().plus(ESTIMATED_DELIVERY_DAYS, ChronoUnit.DAYS));
            deliveryRepository.save(delivery);
            return buildOperation(delivery, MessageConstants.SHIPMENT_CREATED);
        }

        delivery.setStatus(DeliveryStatus.FAILED);
        deliveryRepository.save(delivery);
        return buildOperation(delivery, MessageConstants.SHIPMENT_FAILED);
    }

    private DeliveryOperationDTO toAlreadyProcessedOperation(Delivery delivery) {
        String message = delivery.getStatus() == DeliveryStatus.CREATED
                ? MessageConstants.SHIPMENT_CREATED
                : MessageConstants.SHIPMENT_ALREADY_EXISTS;
        return buildOperation(delivery, message);
    }

    private DeliveryOperationDTO buildOperation(Delivery delivery, String message) {
        DeliveryOperationDTO operation = DeliveryMapper.toDeliveryOperation(delivery);
        operation.setMessage(message);
        return operation;
    }

    private Delivery getShipmentByOrderIdOrThrow(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(
                () -> new ResourceNotFoundException("No shipment found for order id: " + orderId));
    }
}