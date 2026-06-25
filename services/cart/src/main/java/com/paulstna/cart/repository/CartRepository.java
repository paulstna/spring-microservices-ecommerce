package com.paulstna.cart.repository;

import com.paulstna.cart.model.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CartRepository {

    private final RedisTemplate<String, Cart> cartRedisTemplate;

    private static final String KEY_PREFIX = "cart:";
    private static final Duration TTL = Duration.ofDays(7);

    private String key(UUID userId) {
        return KEY_PREFIX + userId;
    }

    public Optional<Cart> findByUserId(UUID userId) {
        return Optional.ofNullable(cartRedisTemplate.opsForValue().get(key(userId)));
    }

    public Cart save(Cart cart) {
        cart.setUpdatedAt(Instant.now());
        cartRedisTemplate.opsForValue().set(key(cart.getUserId()), cart, TTL);
        return cart;
    }

    public void deleteByUserId(UUID userId) {
        cartRedisTemplate.delete(key(userId));
    }
}