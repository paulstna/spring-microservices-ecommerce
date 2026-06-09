package com.paulstna.delivery.service;

import com.paulstna.delivery.exception.ResourceNotFoundException;
import com.paulstna.delivery.constants.MessageConstants;
import com.paulstna.delivery.dto.DeliveryOperationDTO;
import com.paulstna.delivery.dto.DeliveryRequestDTO;
import com.paulstna.delivery.dto.DeliveryResponseDTO;
import com.paulstna.delivery.model.Delivery;
import com.paulstna.delivery.model.DeliveryStatus;
import com.paulstna.delivery.repository.DeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private UUID orderId;
    private UUID userId;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();

        delivery = new Delivery();
        delivery.setId(UUID.randomUUID());
        delivery.setOrderId(orderId);
        delivery.setUserId(userId);
        delivery.setRecipientName("Paul Santana");
        delivery.setStreet("Av. Amazonas 123");
        delivery.setCity("Quito");
        delivery.setState("Pichincha");
        delivery.setPostalCode("170123");
        delivery.setCountry("EC");
        delivery.setCarrier("DHL");
        delivery.setStatus(DeliveryStatus.CREATED);
        delivery.setTrackingNumber("TRK-123");
    }

    private DeliveryRequestDTO buildRequest(String country) {
        return DeliveryRequestDTO.builder()
                .orderId(orderId)
                .userId(userId)
                .recipientName("Paul Santana")
                .street("Av. Amazonas 123")
                .city("Quito")
                .state("Pichincha")
                .postalCode("170123")
                .country(country)
                .carrier("DHL")
                .build();
    }

    @Test
    void createShipment_createsCorrectly_whenCountryIsServiceable() {
        DeliveryRequestDTO request = buildRequest("EC");
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

        DeliveryOperationDTO result = deliveryService.createShipment(request);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.CREATED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.SHIPMENT_CREATED);
        assertThat(result.getTrackingNumber()).isNotNull();
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void createShipment_fails_whenCountryIsNotServiceable() {
        DeliveryRequestDTO request = buildRequest("JP"); // no soportado
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

        DeliveryOperationDTO result = deliveryService.createShipment(request);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.SHIPMENT_FAILED);
        assertThat(result.getTrackingNumber()).isNull();
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void createShipment_isIdempotent_whenShipmentAlreadyExists() {
        DeliveryRequestDTO request = buildRequest("EC");
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryOperationDTO result = deliveryService.createShipment(request);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.CREATED);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        verify(deliveryRepository, never()).save(any()); // no doble envío
    }

    @Test
    void cancelShipment_cancelsCorrectly_whenCreated() {
        delivery.setStatus(DeliveryStatus.CREATED);
        DeliveryOperationDTO operation = DeliveryOperationDTO.builder().orderId(orderId).build();
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(inv -> inv.getArgument(0));

        DeliveryOperationDTO result = deliveryService.cancelShipment(operation);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
        assertThat(result.getMessage()).isEqualTo(MessageConstants.SHIPMENT_CANCELLED);
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
        verify(deliveryRepository).save(delivery);
    }

    @Test
    void cancelShipment_isIdempotent_whenAlreadyCancelled() {
        delivery.setStatus(DeliveryStatus.CANCELLED);
        DeliveryOperationDTO operation = DeliveryOperationDTO.builder().orderId(orderId).build();
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryOperationDTO result = deliveryService.cancelShipment(operation);

        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.CANCELLED);
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void cancelShipment_throwsIllegalState_whenNotCreated() {
        delivery.setStatus(DeliveryStatus.FAILED);
        DeliveryOperationDTO operation = DeliveryOperationDTO.builder().orderId(orderId).build();
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        assertThatThrownBy(() -> deliveryService.cancelShipment(operation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel");
        verify(deliveryRepository, never()).save(any());
    }

    @Test
    void cancelShipment_throwsResourceNotFound_whenShipmentMissing() {
        DeliveryOperationDTO operation = DeliveryOperationDTO.builder().orderId(orderId).build();
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.cancelShipment(operation))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getShipmentByOrderId_returnsDeliveryResponse() {
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.of(delivery));

        DeliveryResponseDTO result = deliveryService.getShipmentByOrderId(orderId);

        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getCountry()).isEqualTo("EC");
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.CREATED);
    }

    @Test
    void getShipmentByOrderId_throwsResourceNotFound_whenMissing() {
        when(deliveryRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deliveryService.getShipmentByOrderId(orderId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}