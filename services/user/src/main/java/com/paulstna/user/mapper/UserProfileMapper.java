package com.paulstna.user.mapper;

import com.paulstna.user.dto.request.UserProfileRequestDto;
import com.paulstna.user.dto.response.UserProfileResponse;
import com.paulstna.user.model.UserProfile;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserProfileMapper {

    public UserProfileResponse toUserProfileResponse(UserProfile user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }

    public UserProfile requestToEntity(UserProfileRequestDto request, UserProfile user) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthdate());
        user.setProfileImageUrl("default.png");
        return user;
    }
}
