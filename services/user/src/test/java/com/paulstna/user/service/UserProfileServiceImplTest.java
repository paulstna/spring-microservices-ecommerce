package com.paulstna.user.service;

import com.paulstna.user.dto.request.UserProfileRequestDTO;
import com.paulstna.user.dto.response.UserProfileResponseDTO;
import com.paulstna.user.exception.ResourceAlreadyExistsException;
import com.paulstna.user.exception.ResourceNotFoundException;
import com.paulstna.user.model.UserProfile;
import com.paulstna.user.model.UserProfileStatus;
import com.paulstna.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UUID userId;
    private UserProfile userProfile;
    private UserProfileRequestDTO requestDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        userProfile = new UserProfile();
        userProfile.setId(userId);
        userProfile.setEmail("test@example.com");
        userProfile.setFirstName("Carlos");
        userProfile.setLastName("Santana");

        requestDto = new UserProfileRequestDTO();
        requestDto.setEmail("test@example.com");
        requestDto.setFirstName("Carlos");
        requestDto.setLastName("Santana");
    }

    @Test
    void shouldReturnUserProfileWhenUserExists() {
        // Arrange
        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(userProfile));

        // Act
        UserProfileResponseDTO result = userProfileService.getUserProfile(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userProfile.getEmail());

        verify(userProfileRepository).findById(userId);
    }

    @Test
    void shouldThrowExceptionWhenUserProfileNotFound() {
        // Arrange
        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userProfileService.getUserProfile(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profile not found");

        verify(userProfileRepository).findById(userId);
    }

    @Test
    void shouldCreateUserProfileSuccessfully() {
        // Arrange
        when(userProfileRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.empty());

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(userProfile);

        // Act
        UserProfileResponseDTO result =
                userProfileService.createProfile(userId, requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userProfile.getEmail());

        verify(userProfileRepository).findByEmail(requestDto.getEmail());
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        UserProfile existingUser = new UserProfile();
        existingUser.setId(UUID.randomUUID());

        when(userProfileRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThatThrownBy(() ->
                userProfileService.createProfile(userId, requestDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email already in use");

        verify(userProfileRepository).findByEmail(requestDto.getEmail());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void shouldUpdateUserProfileSuccessfully() {
        // Arrange
        when(userProfileRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.empty());

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(userProfile));

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(userProfile);

        // Act
        UserProfileResponseDTO result =
                userProfileService.updateProfile(userId, requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userProfile.getEmail());

        verify(userProfileRepository).findByEmail(requestDto.getEmail());
        verify(userProfileRepository).findById(userId);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProfile() {
        // Arrange
        when(userProfileRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.empty());

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                userProfileService.updateProfile(userId, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profile not found");

        verify(userProfileRepository).findByEmail(requestDto.getEmail());
        verify(userProfileRepository).findById(userId);
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailBelongsToAnotherUser() {
        // Arrange
        UserProfile anotherUser = new UserProfile();
        anotherUser.setId(UUID.randomUUID());

        when(userProfileRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(anotherUser));

        // Act & Assert
        assertThatThrownBy(() ->
                userProfileService.updateProfile(userId, requestDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email already in use");

        verify(userProfileRepository).findByEmail(requestDto.getEmail());
        verify(userProfileRepository, never()).findById(any());
        verify(userProfileRepository, never()).save(any());
    }

    @Test
    void shouldAllowUpdatingWhenEmailBelongsToSameUser() {
        // Arrange
        when(userProfileRepository.findByEmail(requestDto.getEmail()))
                .thenReturn(Optional.of(userProfile));

        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(userProfile));

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(userProfile);

        // Act
        UserProfileResponseDTO result =
                userProfileService.updateProfile(userId, requestDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userProfile.getEmail());

        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void shouldDeleteUserProfileSuccessfully() {
        // Arrange
        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(userProfile);

        // Act
        userProfileService.deleteProfile(userId);

        // Assert
        verify(userProfileRepository).findById(userId);
        verify(userProfileRepository).save(userProfile);
        assertThat(userProfile.getStatus()).isEqualTo(UserProfileStatus.PENDING_DELETION);
        verify(userProfileRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProfile() {
        // Arrange
        when(userProfileRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                userProfileService.deleteProfile(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profile not found");

        verify(userProfileRepository).findById(userId);
        verify(userProfileRepository, never()).delete(any());
    }
}