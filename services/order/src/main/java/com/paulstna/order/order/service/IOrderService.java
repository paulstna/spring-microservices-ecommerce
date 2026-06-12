package com.paulstna.order.order.service;

import com.paulstna.order.order.dto.OrderResponseDTO;
import com.paulstna.order.order.dto.PlaceOrderRequestDTO;
import com.paulstna.order.order.model.OrderFailureReason;
import com.paulstna.order.order.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface IOrderService {

    OrderResponseDTO placeOrder(PlaceOrderRequestDTO request);

    OrderResponseDTO getOrderById(UUID orderId);

    List<OrderResponseDTO> getOrdersByUserId(UUID userId);

    OrderResponseDTO cancelOrder(UUID orderId);

    OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus newStatus,
                                       OrderFailureReason failureReason);
}