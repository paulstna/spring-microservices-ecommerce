package com.paulstna.notification.controller;

import com.paulstna.notification.dto.NotificationResponseDTO;
import com.paulstna.notification.service.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/{version}/notifications", version = "v1")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getByUser(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getByUserId(userId));
    }
}