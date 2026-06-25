package com.paulstna.cart.service;

import com.paulstna.cart.exception.ResourceNotFoundException;
import com.paulstna.cart.dto.CartItemRequestDTO;
import com.paulstna.cart.dto.CartResponseDTO;
import com.paulstna.cart.mapper.CartMapper;
import com.paulstna.cart.model.Cart;
import com.paulstna.cart.model.CartItem;
import com.paulstna.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {

    private final CartRepository cartRepository;

    @Override
    public CartResponseDTO getCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> emptyCart(userId));
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponseDTO addItem(UUID userId, CartItemRequestDTO request) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> emptyCart(userId));

        cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + request.getQuantity()),
                        () -> cart.getItems().add(new CartItem(
                                request.getProductId(), request.getProductName(),
                                request.getQuantity(), request.getUnitPrice()))
                );

        return CartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponseDTO updateItemQuantity(UUID userId, UUID productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for user " + userId));
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product " + productId + " is not in the cart"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(quantity);
        }
        return CartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public CartResponseDTO removeItem(UUID userId, UUID productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for user " + userId));
        cart.getItems().removeIf(i -> i.getProductId().equals(productId));
        return CartMapper.toResponse(cartRepository.save(cart));
    }

    @Override
    public void clearCart(UUID userId) {
        cartRepository.deleteByUserId(userId);
    }

    private Cart emptyCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setItems(new ArrayList<>());
        return cart;
    }
}