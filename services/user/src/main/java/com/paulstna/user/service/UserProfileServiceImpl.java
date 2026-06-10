package com.paulstna.user.service;

import com.paulstna.user.dto.request.UserProfileRequestDTO;
import com.paulstna.user.dto.response.UserProfileResponseDTO;
import com.paulstna.user.exception.ResourceAlreadyExistsException;
import com.paulstna.user.exception.ResourceNotFoundException;
import com.paulstna.user.mapper.UserProfileMapper;
import com.paulstna.user.model.UserProfile;
import com.paulstna.user.model.UserProfileStatus;
import com.paulstna.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements IUserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserProfileResponseDTO getUserProfile(UUID userId) {
        return UserProfileMapper
                .toUserProfileResponse(getUserProfileEntity(userId));
    }

    @Override
    public UserProfileResponseDTO createProfile(UUID userId, UserProfileRequestDTO requestDto) {
        validateEmailUniqueness(requestDto.getEmail(), null);
        UserProfile userProfile = UserProfileMapper
                .requestToEntity(requestDto, new UserProfile());
        userProfile.setId(userId);
        return UserProfileMapper.toUserProfileResponse(
                userProfileRepository.save(userProfile)
        );
    }

    @Override
    public UserProfileResponseDTO updateProfile(UUID userId, UserProfileRequestDTO requestDto) {
        validateEmailUniqueness(requestDto.getEmail(), userId);
        UserProfile userProfile = getUserProfileEntity(userId);
        userProfile = UserProfileMapper
                .requestToEntity(requestDto, userProfile);
        return UserProfileMapper
                .toUserProfileResponse(userProfileRepository.save(userProfile));
    }

    @Override
    public void deleteProfile(UUID userId) {
        UserProfile profile = getUserProfileEntity(userId);
        profile.setStatus(UserProfileStatus.PENDING_DELETION);
        userProfileRepository.save(profile);
    }

    @Override
    public UserProfile getUserProfileEntity(UUID userId) {
        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    private void validateEmailUniqueness(String email, UUID currentUserId) {
        Optional<UserProfile> existingUser = userProfileRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUserId)) {
            throw new ResourceAlreadyExistsException("Email already in use");
        }
    }
}
