package com.paulstna.cart.mapper;

import com.paulstna.cart.dto.CartItemResponseDTO;
import com.paulstna.cart.dto.CartResponseDTO;
import com.paulstna.cart.model.Cart;
import com.paulstna.cart.model.CartItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class CartMapper {

    public CartResponseDTO toResponse(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(CartMapper::toItemResponse)
                .toList();
        BigDecimal total = items.stream()
                .map(CartItemResponseDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartResponseDTO.builder()
                .userId(cart.getUserId())
                .items(items)
                .totalAmount(total)
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private CartItemResponseDTO toItemResponse(CartItem item) {
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponseDTO.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }
}