package com.paulstna.cart.service;

import com.paulstna.cart.exception.ResourceNotFoundException;
import com.paulstna.cart.dto.CartItemRequestDTO;
import com.paulstna.cart.dto.CartResponseDTO;
import com.paulstna.cart.model.Cart;
import com.paulstna.cart.model.CartItem;
import com.paulstna.cart.repository.CartRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @InjectMocks
    private CartServiceImpl service;

    private final UUID userId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    private CartItemRequestDTO itemRequest(int qty) {
        CartItemRequestDTO req = new CartItemRequestDTO();
        req.setProductId(productId);
        req.setProductName("Camiseta");
        req.setQuantity(qty);
        req.setUnitPrice(new BigDecimal("10.00"));
        return req;
    }

    @Test
    void addItem_newProduct_addsToEmptyCart() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponseDTO result = service.addItem(userId, itemRequest(2));

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("20.00");
    }

    @Test
    void addItem_existingProduct_incrementsQuantity() {
        Cart existing = new Cart();
        existing.setUserId(userId);
        existing.setItems(new ArrayList<>(List.of(
                new CartItem(productId, "Camiseta", 1, new BigDecimal("10.00")))));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponseDTO result = service.addItem(userId, itemRequest(2));

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void updateItemQuantity_zero_removesItem() {
        Cart existing = new Cart();
        existing.setUserId(userId);
        existing.setItems(new ArrayList<>(List.of(
                new CartItem(productId, "Camiseta", 5, new BigDecimal("10.00")))));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponseDTO result = service.updateItemQuantity(userId, productId, 0);

        assertThat(result.getItems()).isEmpty();
    }

    @Test
    void updateItemQuantity_missingCart_throwsNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateItemQuantity(userId, productId, 3))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}