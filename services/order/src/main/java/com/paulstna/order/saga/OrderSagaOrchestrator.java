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
import com.paulstna.order.saga.event.OrderConfirmedEvent;
import com.paulstna.order.saga.event.OrderEventPublisher;
import com.paulstna.order.saga.event.OrderFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaOrchestrator {

    private final IOrderService orderService;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final OrderEventPublisher eventPublisher;

    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request, String correlationId) {
        OrderResponseDTO order = orderService.placeOrder(request);
        log.info("Saga started for order {} (correlationId={})", order.getOrderId(), correlationId);
        return process(order, correlationId);
    }

    private OrderResponseDTO process(OrderResponseDTO order, String correlationId) {
        UUID orderId = order.getOrderId();
        List<UUID> reservedProductIds = new ArrayList<>();
        boolean paymentAuthorized = false;

        try {
            for (OrderItemResponseDTO item : order.getItems()) {
                StockReservationResult result = inventoryClient.reserve(correlationId,
                        new StockReservationRequest(item.getProductId(), item.getQuantity(), orderId));
                if (result.status() != StockStatus.RESERVED) {
                    log.warn("Order {}: insufficient stock for product {}", orderId, item.getProductId());
                    releaseInventory(reservedProductIds, orderId, correlationId);
                    return fail(order, OrderFailureReason.OUT_OF_STOCK, correlationId);
                }
                reservedProductIds.add(item.getProductId());
            }
            orderService.updateOrderStatus(orderId, OrderStatus.INVENTORY_RESERVED, null);

            PaymentOperationResult auth = paymentClient.authorize(correlationId,
                    new PaymentAuthorizeRequest(orderId, order.getUserId(), order.getTotalAmount(),
                            order.getCurrency(), order.getPaymentMethod()));
            if (auth.status() != PaymentStatus.AUTHORIZED) {
                log.warn("Order {}: payment declined", orderId);
                releaseInventory(reservedProductIds, orderId, correlationId);
                return fail(order, OrderFailureReason.PAYMENT_FAILED, correlationId);
            }
            paymentAuthorized = true;
            paymentClient.capture(correlationId, new PaymentOperationRequest(orderId));
            orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_CHARGED, null);

            DeliveryOperationResult shipment = deliveryClient.createShipment(correlationId, toDeliveryRequest(order));
            if (shipment.status() != DeliveryStatus.CREATED) {
                log.warn("Order {}: shipment creation failed", orderId);
                safeRefund(orderId, correlationId);
                releaseInventory(reservedProductIds, orderId, correlationId);
                return fail(order, OrderFailureReason.DELIVERY_FAILED, correlationId);
            }

            confirmInventory(reservedProductIds, orderId, correlationId);
            OrderResponseDTO confirmed = orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED, null);
            eventPublisher.publishOrderConfirmed(new OrderConfirmedEvent(
                    orderId, order.getUserId(), order.getTotalAmount(), Instant.now()));
            log.info("Saga completed for order {}: CONFIRMED", orderId);
            return confirmed;

        } catch (Exception ex) {
            log.error("Saga error for order {}: {}", orderId, ex.getMessage(), ex);
            if (paymentAuthorized) {
                safeRefund(orderId, correlationId);
            }
            releaseInventory(reservedProductIds, orderId, correlationId);
            return fail(order, OrderFailureReason.SAGA_ERROR, correlationId);
        }
    }

    private void releaseInventory(List<UUID> productIds, UUID orderId, String correlationId) {
        for (UUID productId : productIds) {
            try {
                inventoryClient.release(correlationId, new StockReservationRequest(productId, null, orderId));
            } catch (Exception ex) {
                log.error("Compensation failed: release product {} order {}: {}",
                        productId, orderId, ex.getMessage());
            }
        }
    }

    private void confirmInventory(List<UUID> productIds, UUID orderId, String correlationId) {
        for (UUID productId : productIds) {
            try {
                inventoryClient.confirm(correlationId, new StockReservationRequest(productId, null, orderId));
            } catch (Exception ex) {
                log.error("Inventory confirm failed for product {} order {}: {} (needs reconciliation)",
                        productId, orderId, ex.getMessage());
            }
        }
    }

    private void safeRefund(UUID orderId, String correlationId) {
        try {
            paymentClient.refund(correlationId, new PaymentOperationRequest(orderId));
        } catch (Exception ex) {
            log.error("Compensation failed: refund order {}: {}", orderId, ex.getMessage());
        }
    }

    private OrderResponseDTO fail(OrderResponseDTO order, OrderFailureReason reason, String correlationId) {
        OrderResponseDTO failed = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.FAILED, reason);
        eventPublisher.publishOrderFailed(new OrderFailedEvent(
                order.getOrderId(), order.getUserId(), reason, Instant.now()));
        log.info("Saga ended for order {}: FAILED ({})", order.getOrderId(), reason);
        return failed;
    }

    private DeliveryCreateRequest toDeliveryRequest(OrderResponseDTO order) {
        return new DeliveryCreateRequest(
                order.getOrderId(), order.getUserId(), order.getRecipientName(),
                order.getStreet(), order.getCity(), order.getState(),
                order.getPostalCode(), order.getCountry(), null);
    }
}