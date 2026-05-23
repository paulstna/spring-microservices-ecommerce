package com.paulstna.user.controller;

import com.paulstna.user.dto.request.UserProfileRequestDto;
import com.paulstna.user.dto.response.UserProfileResponse;
import com.paulstna.user.service.IUserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/{version}/profiles", version = "v1")
public class UserProfileController {

    private final IUserProfileService userProfileService;

    @GetMapping(path = "/me")
    public ResponseEntity<UserProfileResponse> getUserProfile(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId) {

        return ResponseEntity.ok(userProfileService.getUserProfile(userId));
    }

    @PostMapping
    public ResponseEntity<UserProfileResponse> createUserProfile(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @RequestBody @Valid UserProfileRequestDto userProfileRequestDto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userProfileService.createProfile(userId, userProfileRequestDto));
    }

    @PutMapping(path = "/me")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId,
            @RequestBody @Valid UserProfileRequestDto userProfileRequestDto) {

        return ResponseEntity
                .ok(userProfileService.updateProfile(userId, userProfileRequestDto));
    }

    @DeleteMapping(path = "/me")
    public ResponseEntity<Void> requestDeletion(
            @RequestHeader("X-paulstna-Correlation-ID") String correlationId,
            @RequestHeader("X-paulstna-User-ID") UUID userId) {

        userProfileService.deleteProfile(userId);
        return ResponseEntity.noContent().build();
    }
}
