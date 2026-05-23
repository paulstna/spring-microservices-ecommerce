package com.paulstna.user.service;

import com.paulstna.user.dto.request.UserProfileRequestDto;
import com.paulstna.user.dto.response.UserProfileResponse;
import com.paulstna.user.model.UserProfile;

import java.util.UUID;

public interface IUserProfileService {
    UserProfileResponse getUserProfile(UUID userId);

    UserProfileResponse createProfile(UUID userId, UserProfileRequestDto userProfileRequestDto);

    UserProfileResponse updateProfile(UUID userId, UserProfileRequestDto userProfileRequestDto);

    void deleteProfile(UUID userId);

    UserProfile getUserProfileEntity(UUID userId);
}
