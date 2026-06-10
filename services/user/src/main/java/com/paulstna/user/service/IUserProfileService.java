package com.paulstna.user.service;

import com.paulstna.user.dto.request.UserProfileRequestDTO;
import com.paulstna.user.dto.response.UserProfileResponseDTO;
import com.paulstna.user.model.UserProfile;

import java.util.UUID;

public interface IUserProfileService {
    UserProfileResponseDTO getUserProfile(UUID userId);

    UserProfileResponseDTO createProfile(UUID userId, UserProfileRequestDTO userProfileRequestDto);

    UserProfileResponseDTO updateProfile(UUID userId, UserProfileRequestDTO userProfileRequestDto);

    void deleteProfile(UUID userId);

    UserProfile getUserProfileEntity(UUID userId);
}
