package com.paulstna.order.order.service;

import com.paulstna.order.common.exception.ResourceNotFoundException;
import com.paulstna.order.order.constants.MessageConstants;
import com.paulstna.order.order.dto.OrderResponseDTO;
import com.paulstna.order.order.dto.PlaceOrderRequestDTO;
import com.paulstna.order.order.mapper.OrderMapper;
import com.paulstna.order.order.model.Order;
import com.paulstna.order.order.model.OrderFailureReason;
import com.paulstna.order.order.model.OrderStatus;
import com.paulstna.order.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderResponseDTO placeOrder(PlaceOrderRequestDTO request) {
        Order order = OrderMapper.toEntity(request);
        order.setStatus(OrderStatus.CREATED);
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }

    @Override
    public OrderResponseDTO getOrderById(UUID orderId) {
        return OrderMapper.toOrderResponse(getOrderByIdOrThrow(orderId));
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }

    @Transactional
    @Override
    public OrderResponseDTO cancelOrder(UUID orderId) {
        Order order = getOrderByIdOrThrow(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return OrderMapper.toOrderResponse(order);
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException(MessageConstants.CANNOT_CANCEL + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus newStatus,
                                              OrderFailureReason failureReason) {
        Order order = getOrderByIdOrThrow(orderId);

        if (!isValidTransition(order.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    MessageConstants.INVALID_TRANSITION + order.getStatus() + " -> " + newStatus);
        }

        order.setStatus(newStatus);
        if (newStatus == OrderStatus.FAILED) {
            order.setFailureReason(failureReason);
        }
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case CREATED -> to == OrderStatus.INVENTORY_RESERVED
                    || to == OrderStatus.FAILED
                    || to == OrderStatus.CANCELLED;
            case INVENTORY_RESERVED -> to == OrderStatus.PAYMENT_CHARGED
                    || to == OrderStatus.FAILED;
            case PAYMENT_CHARGED -> to == OrderStatus.CONFIRMED
                    || to == OrderStatus.FAILED;
            case CONFIRMED, FAILED, CANCELLED -> false;
        };
    }

    private Order getOrderByIdOrThrow(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException(MessageConstants.ORDER_NOT_FOUND + orderId));
    }
}