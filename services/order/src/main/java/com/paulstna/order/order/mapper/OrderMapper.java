package com.paulstna.order.order.mapper;

import com.paulstna.order.order.dto.OrderItemRequestDTO;
import com.paulstna.order.order.dto.OrderItemResponseDTO;
import com.paulstna.order.order.dto.OrderResponseDTO;
import com.paulstna.order.order.dto.PlaceOrderRequestDTO;
import com.paulstna.order.order.model.Order;
import com.paulstna.order.order.model.OrderItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class OrderMapper {

    public Order toEntity(PlaceOrderRequestDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setCurrency(dto.getCurrency());
        order.setRecipientName(dto.getRecipientName());
        order.setStreet(dto.getStreet());
        order.setCity(dto.getCity());
        order.setState(dto.getState());
        order.setPostalCode(dto.getPostalCode());
        order.setCountry(dto.getCountry());
        order.setPaymentMethod(dto.getPaymentMethod());

        List<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> toItemEntity(itemDto, order))
                .toList();
        order.setItems(new ArrayList<>(items));

        BigDecimal total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        return order;
    }

    private OrderItem toItemEntity(OrderItemRequestDTO dto, Order order) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(dto.getProductId());
        item.setProductName(dto.getProductName());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.setSubtotal(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        return item;
    }

    public OrderResponseDTO toOrderResponse(Order order) {
        List<OrderItemResponseDTO> items = order.getItems().stream()
                .map(OrderMapper::toItemResponse)
                .toList();

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .recipientName(order.getRecipientName())
                .street(order.getStreet())
                .city(order.getCity())
                .state(order.getState())
                .postalCode(order.getPostalCode())
                .country(order.getCountry())
                .paymentMethod(order.getPaymentMethod())
                .failureReason(order.getFailureReason())
                .items(items)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponseDTO toItemResponse(OrderItem item) {
        return OrderItemResponseDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}