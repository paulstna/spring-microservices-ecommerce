package com.paulstna.user.service;

import com.paulstna.user.dto.request.CreateAddressRequest;
import com.paulstna.user.dto.response.AddressResponse;
import com.paulstna.user.exception.ResourceNotFoundException;
import com.paulstna.user.model.Address;
import com.paulstna.user.model.UserProfile;
import com.paulstna.user.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private IUserProfileService userProfileService;

    @InjectMocks
    private AddressServiceImpl addressService;

    private UUID userId;
    private UUID addressId;

    private UserProfile userProfile;
    private Address address;

    private CreateAddressRequest createAddressRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        userProfile = new UserProfile();
        userProfile.setId(userId);

        address = new Address();
        address.setId(addressId);
        address.setUserProfile(userProfile);
        address.setDefault(false);
        address.setCity("Quito");
        address.setStreet("Main Street");

        createAddressRequest = new CreateAddressRequest();
        createAddressRequest.setCity("Quito");
        createAddressRequest.setStreet("Main Street");
    }

    // ─────────────────────────────────────────────────────────────
    // getUserAddresses
    // ─────────────────────────────────────────────────────────────

    @Test
    void shouldReturnUserAddresses() {
        // Arrange
        Address secondAddress = new Address();
        secondAddress.setId(UUID.randomUUID());

        when(addressRepository.findByUserProfileId(userId))
                .thenReturn(List.of(address, secondAddress));

        // Act
        List<AddressResponse> result =
                addressService.getUserAddresses(userId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(addressRepository).findByUserProfileId(userId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoAddresses() {
        // Arrange
        when(addressRepository.findByUserProfileId(userId))
                .thenReturn(List.of());

        // Act
        List<AddressResponse> result =
                addressService.getUserAddresses(userId);

        // Assert
        assertThat(result).isNotNull().isEmpty();

        verify(addressRepository).findByUserProfileId(userId);
    }

    // ─────────────────────────────────────────────────────────────
    // getUserAddress
    // ─────────────────────────────────────────────────────────────

    @Test
    void shouldReturnUserAddress() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.of(address));

        // Act
        AddressResponse result =
                addressService.getUserAddress(userId, addressId);

        // Assert
        assertThat(result).isNotNull();

        verify(addressRepository)
                .findByUserProfileIdAndId(userId, addressId);
    }

    @Test
    void shouldThrowExceptionWhenAddressNotFound() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                addressService.getUserAddress(userId, addressId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address " + addressId + " not found");

        verify(addressRepository)
                .findByUserProfileIdAndId(userId, addressId);
    }

    // ─────────────────────────────────────────────────────────────
    // createAddress
    // ─────────────────────────────────────────────────────────────

    @Test
    void shouldCreateAddressSuccessfully() {
        // Arrange
        when(userProfileService.getUserProfileEntity(userId))
                .thenReturn(userProfile);

        when(addressRepository.save(any(Address.class)))
                .thenReturn(address);

        // Act
        AddressResponse result =
                addressService.createAddress(userId, createAddressRequest);

        // Assert
        assertThat(result).isNotNull();

        verify(userProfileService).getUserProfileEntity(userId);
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void shouldThrowExceptionWhenUserProfileNotFound() {
        // Arrange
        when(userProfileService.getUserProfileEntity(userId))
                .thenThrow(new ResourceNotFoundException("Profile not found"));

        // Act & Assert
        assertThatThrownBy(() ->
                addressService.createAddress(userId, createAddressRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Profile not found");

        verify(userProfileService).getUserProfileEntity(userId);
        verify(addressRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────
    // updateAddress
    // ─────────────────────────────────────────────────────────────

    @Test
    void shouldUpdateAddressSuccessfully() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(any(Address.class)))
                .thenReturn(address);

        // Act
        AddressResponse result =
                addressService.updateAddress(
                        userId,
                        addressId,
                        createAddressRequest
                );

        // Assert
        assertThat(result).isNotNull();

        verify(addressRepository)
                .findByUserProfileIdAndId(userId, addressId);

        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingAddress() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                addressService.updateAddress(
                        userId,
                        addressId,
                        createAddressRequest
                ))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address " + addressId + " not found");

        verify(addressRepository)
                .findByUserProfileIdAndId(userId, addressId);

        verify(addressRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────
    // setDefaultAddress
    // ─────────────────────────────────────────────────────────────

    @Test
    void shouldSetNewDefaultAddressSuccessfully() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.of(address));

        when(addressRepository.findByUserProfileIdAndIsDefaultTrue(userId))
                .thenReturn(Optional.empty());

        when(addressRepository.save(any(Address.class)))
                .thenReturn(address);

        // Act
        AddressResponse result =
                addressService.setDefaultAddress(userId, addressId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(address.isDefault()).isTrue();

        verify(addressRepository, times(1))
                .save(any(Address.class));
    }

    @Test
    void shouldUnsetPreviousDefaultAddress() {
        // Arrange
        Address previousDefault = new Address();
        previousDefault.setId(UUID.randomUUID());
        previousDefault.setDefault(true);

        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.of(address));

        when(addressRepository.findByUserProfileIdAndIsDefaultTrue(userId))
                .thenReturn(Optional.of(previousDefault));

        when(addressRepository.save(any(Address.class)))
                .thenReturn(address);

        // Act
        AddressResponse result =
                addressService.setDefaultAddress(userId, addressId);

        // Assert
        assertThat(result).isNotNull();

        assertThat(previousDefault.isDefault()).isFalse();
        assertThat(address.isDefault()).isTrue();

        verify(addressRepository, times(2))
                .save(any(Address.class));
    }

    @Test
    void shouldKeepSameAddressAsDefaultWhenAlreadyDefault() {
        // Arrange
        address.setDefault(true);

        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.of(address));

        when(addressRepository.findByUserProfileIdAndIsDefaultTrue(userId))
                .thenReturn(Optional.of(address));

        when(addressRepository.save(any(Address.class)))
                .thenReturn(address);

        // Act
        AddressResponse result =
                addressService.setDefaultAddress(userId, addressId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(address.isDefault()).isTrue();

        verify(addressRepository, times(1))
                .save(any(Address.class));
    }

    @Test
    void shouldThrowExceptionWhenDefaultAddressNotFound() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                addressService.setDefaultAddress(userId, addressId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address " + addressId + " not found");

        verify(addressRepository)
                .findByUserProfileIdAndId(userId, addressId);

        verify(addressRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────
    // deleteAddress
    // ─────────────────────────────────────────────────────────────

    @Test
    void shouldDeleteAddressSuccessfully() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.of(address));

        // Act
        addressService.deleteAddress(userId, addressId);

        // Assert
        verify(addressRepository)
                .findByUserProfileIdAndId(userId, addressId);

        verify(addressRepository).delete(address);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingAddress() {
        // Arrange
        when(addressRepository.findByUserProfileIdAndId(userId, addressId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() ->
                addressService.deleteAddress(userId, addressId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Address " + addressId + " not found");

        verify(addressRepository)
                .findByUserProfileIdAndId(userId, addressId);

        verify(addressRepository, never()).delete(any());
    }
}