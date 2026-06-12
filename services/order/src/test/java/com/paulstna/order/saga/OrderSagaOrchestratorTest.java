package com.paulstna.order.saga;

import com.paulstna.order.order.dto.OrderItemResponseDTO;
import com.paulstna.order.order.dto.OrderResponseDTO;
import com.paulstna.order.order.dto.PlaceOrderRequestDTO;
import com.paulstna.order.order.model.OrderFailureReason;
import com.paulstna.order.order.model.OrderStatus;
import com.paulstna.order.order.service.IOrderService;
import com.paulstna.order.saga.client.DeliveryClient;
import com.paulstna.order.saga.client.InventoryClient;
import com.paulstna.order.saga.client.PaymentClient;
import com.paulstna.order.saga.client.dto.*;
import com.paulstna.order.saga.event.OrderEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderSagaOrchestratorTest {

    @Mock
    private IOrderService orderService;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private DeliveryClient deliveryClient;

    @Mock
    private OrderEventPublisher eventPublisher;

    @InjectMocks
    private OrderSagaOrchestrator orchestrator;

    private UUID orderId;
    private UUID userId;
    private UUID productId;
    private final PlaceOrderRequestDTO request = PlaceOrderRequestDTO.builder().build();

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();

        lenient().when(orderService.updateOrderStatus(any(), any(), any()))
                .thenAnswer(inv -> buildOrder(inv.getArgument(1)));
    }

    private OrderResponseDTO buildOrder(OrderStatus status) {
        OrderItemResponseDTO item = OrderItemResponseDTO.builder()
                .productId(productId).productName("Camiseta").quantity(2)
                .unitPrice(new BigDecimal("10.00")).subtotal(new BigDecimal("20.00")).build();
        return OrderResponseDTO.builder()
                .orderId(orderId).userId(userId).status(status)
                .totalAmount(new BigDecimal("20.00")).currency("USD").paymentMethod("CREDIT_CARD")
                .recipientName("Paul").street("Av. Amazonas").city("Quito").state("Pichincha")
                .postalCode("170123").country("EC")
                .items(List.of(item)).build();
    }

    @Test
    void placeOrder_confirmsOrder_onHappyPath() {
        when(orderService.placeOrder(any())).thenReturn(buildOrder(OrderStatus.CREATED));
        when(inventoryClient.reserve(anyString(), any(StockReservationRequest.class)))
                .thenReturn(new StockReservationResult(StockStatus.RESERVED, "ok"));
        when(paymentClient.authorize(anyString(), any(PaymentAuthorizeRequest.class)))
                .thenReturn(new PaymentOperationResult(PaymentStatus.AUTHORIZED, "TXN-1", "ok"));
        when(deliveryClient.createShipment(anyString(), any(DeliveryCreateRequest.class)))
                .thenReturn(new DeliveryOperationResult(DeliveryStatus.CREATED, "TRK-1", "ok"));

        OrderResponseDTO result = orchestrator.placeOrder(request, "corr-1");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderService).updateOrderStatus(orderId, OrderStatus.INVENTORY_RESERVED, null);
        verify(orderService).updateOrderStatus(orderId, OrderStatus.PAYMENT_CHARGED, null);
        verify(orderService).updateOrderStatus(orderId, OrderStatus.CONFIRMED, null);
        verify(paymentClient).capture(eq("corr-1"), any(PaymentOperationRequest.class));
        verify(inventoryClient).confirm(eq("corr-1"), any(StockReservationRequest.class));
        verify(eventPublisher).publishOrderConfirmed(any());
        verify(inventoryClient, never()).release(anyString(), any());
        verify(paymentClient, never()).refund(anyString(), any());
    }

    @Test
    void placeOrder_failsOutOfStock_whenReserveInsufficient() {
        when(orderService.placeOrder(any())).thenReturn(buildOrder(OrderStatus.CREATED));
        when(inventoryClient.reserve(anyString(), any(StockReservationRequest.class)))
                .thenReturn(new StockReservationResult(StockStatus.INSUFFICIENT, "no stock"));

        OrderResponseDTO result = orchestrator.placeOrder(request, "corr-1");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.FAILED);
        verify(orderService).updateOrderStatus(orderId, OrderStatus.FAILED, OrderFailureReason.OUT_OF_STOCK);
        verify(paymentClient, never()).authorize(anyString(), any());
        verify(deliveryClient, never()).createShipment(anyString(), any());
        verify(inventoryClient, never()).release(anyString(), any());
        verify(eventPublisher).publishOrderFailed(any());
    }

    @Test
    void placeOrder_failsPayment_andReleasesInventory_whenDeclined() {
        when(orderService.placeOrder(any())).thenReturn(buildOrder(OrderStatus.CREATED));
        when(inventoryClient.reserve(anyString(), any(StockReservationRequest.class)))
                .thenReturn(new StockReservationResult(StockStatus.RESERVED, "ok"));
        when(paymentClient.authorize(anyString(), any(PaymentAuthorizeRequest.class)))
                .thenReturn(new PaymentOperationResult(PaymentStatus.FAILED, null, "declined"));

        OrderResponseDTO result = orchestrator.placeOrder(request, "corr-1");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.FAILED);
        verify(orderService).updateOrderStatus(orderId, OrderStatus.FAILED, OrderFailureReason.PAYMENT_FAILED);
        verify(inventoryClient).release(eq("corr-1"), any(StockReservationRequest.class));
        verify(paymentClient, never()).capture(anyString(), any());
        verify(paymentClient, never()).refund(anyString(), any());
        verify(deliveryClient, never()).createShipment(anyString(), any());
        verify(eventPublisher).publishOrderFailed(any());
    }

    @Test
    void placeOrder_failsDelivery_andCompensatesPaymentAndInventory() {
        when(orderService.placeOrder(any())).thenReturn(buildOrder(OrderStatus.CREATED));
        when(inventoryClient.reserve(anyString(), any(StockReservationRequest.class)))
                .thenReturn(new StockReservationResult(StockStatus.RESERVED, "ok"));
        when(paymentClient.authorize(anyString(), any(PaymentAuthorizeRequest.class)))
                .thenReturn(new PaymentOperationResult(PaymentStatus.AUTHORIZED, "TXN-1", "ok"));
        when(deliveryClient.createShipment(anyString(), any(DeliveryCreateRequest.class)))
                .thenReturn(new DeliveryOperationResult(DeliveryStatus.FAILED, null, "not serviceable"));

        OrderResponseDTO result = orchestrator.placeOrder(request, "corr-1");

        assertThat(result.getStatus()).isEqualTo(OrderStatus.FAILED);
        verify(orderService).updateOrderStatus(orderId, OrderStatus.FAILED, OrderFailureReason.DELIVERY_FAILED);
        verify(paymentClient).capture(eq("corr-1"), any(PaymentOperationRequest.class));
        verify(paymentClient).refund(eq("corr-1"), any(PaymentOperationRequest.class));
        verify(inventoryClient).release(eq("corr-1"), any(StockReservationRequest.class));
        verify(inventoryClient, never()).confirm(anyString(), any());
        verify(eventPublisher).publishOrderFailed(any());
    }

}