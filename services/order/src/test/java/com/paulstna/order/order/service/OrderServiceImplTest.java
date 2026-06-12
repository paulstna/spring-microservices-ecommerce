package com.paulstna.order.order.service;

import com.paulstna.order.common.exception.ResourceNotFoundException;
import com.paulstna.order.order.dto.OrderItemRequestDTO;
import com.paulstna.order.order.dto.OrderResponseDTO;
import com.paulstna.order.order.dto.PlaceOrderRequestDTO;
import com.paulstna.order.order.model.Order;
import com.paulstna.order.order.model.OrderFailureReason;
import com.paulstna.order.order.model.OrderItem;
import com.paulstna.order.order.model.OrderStatus;
import com.paulstna.order.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID orderId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    private PlaceOrderRequestDTO buildRequest() {
        OrderItemRequestDTO item1 = OrderItemRequestDTO.builder()
                .productId(UUID.randomUUID())
                .productName("Camiseta Nike")
                .quantity(2)
                .unitPrice(new BigDecimal("10.00"))
                .build();
        OrderItemRequestDTO item2 = OrderItemRequestDTO.builder()
                .productId(UUID.randomUUID())
                .productName("Gorra")
                .quantity(3)
                .unitPrice(new BigDecimal("5.50"))
                .build();

        return PlaceOrderRequestDTO.builder()
                .userId(userId)
                .currency("USD")
                .recipientName("Paul Santana")
                .street("Av. Amazonas 123")
                .city("Quito")
                .state("Pichincha")
                .postalCode("170123")
                .country("EC")
                .items(List.of(item1, item2))
                .build();
    }

    private Order buildOrder(OrderStatus status) {
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setStatus(status);
        order.setTotalAmount(new BigDecimal("36.50"));
        order.setCurrency("USD");
        order.setRecipientName("Paul Santana");
        order.setStreet("Av. Amazonas 123");
        order.setCity("Quito");
        order.setPostalCode("170123");
        order.setCountry("EC");

        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setOrder(order);
        item.setProductId(UUID.randomUUID());
        item.setProductName("Camiseta Nike");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));
        item.setSubtotal(new BigDecimal("20.00"));
        order.setItems(new ArrayList<>(List.of(item)));
        return order;
    }

    @Test
    void placeOrder_createsOrderWithCreatedStatusAndComputedTotal() {
        PlaceOrderRequestDTO request = buildRequest();
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO result = orderService.placeOrder(request);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("36.50"); // 2*10 + 3*5.50
        assertThat(result.getItems()).hasSize(2);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrderById_returnsOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(buildOrder(OrderStatus.CREATED)));

        OrderResponseDTO result = orderService.getOrderById(orderId);

        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void getOrderById_throwsResourceNotFound_whenMissing() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(orderId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getOrdersByUserId_returnsOrders() {
        when(orderRepository.findByUserId(userId))
                .thenReturn(List.of(buildOrder(OrderStatus.CREATED), buildOrder(OrderStatus.CONFIRMED)));

        List<OrderResponseDTO> result = orderService.getOrdersByUserId(userId);

        assertThat(result).hasSize(2);
    }

    @Test
    void cancelOrder_cancelsCorrectly_whenCreated() {
        Order order = buildOrder(OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO result = orderService.cancelOrder(orderId);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_isIdempotent_whenAlreadyCancelled() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(buildOrder(OrderStatus.CANCELLED)));

        OrderResponseDTO result = orderService.cancelOrder(orderId);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_throwsIllegalState_whenAlreadyConfirmed() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(buildOrder(OrderStatus.CONFIRMED)));

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot cancel");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_throwsResourceNotFound_whenMissing() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(orderId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateOrderStatus_transitionsCorrectly_fromCreatedToInventoryReserved() {
        Order order = buildOrder(OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO result = orderService.updateOrderStatus(
                orderId, OrderStatus.INVENTORY_RESERVED, null);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.INVENTORY_RESERVED);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_setsFailureReason_whenFailed() {
        Order order = buildOrder(OrderStatus.INVENTORY_RESERVED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponseDTO result = orderService.updateOrderStatus(
                orderId, OrderStatus.FAILED, OrderFailureReason.PAYMENT_FAILED);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(result.getFailureReason()).isEqualTo(OrderFailureReason.PAYMENT_FAILED);
    }

    @Test
    void updateOrderStatus_throwsIllegalState_onInvalidTransition() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(buildOrder(OrderStatus.CREATED)));

        // CREATED -> CONFIRMED is not a valid transition (intermediate steps are required).
        assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid status transition");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_throwsResourceNotFound_whenMissing() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.updateOrderStatus(orderId, OrderStatus.INVENTORY_RESERVED, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}