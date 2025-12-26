package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.CreateProfileRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateProfileRequest;
import ru.skillfactory.learning.platform.dto.response.ProfileResponse;
import ru.skillfactory.learning.platform.entity.Profile;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.ProfileMapper;
import ru.skillfactory.learning.platform.repository.ProfileRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.impl.ProfileServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private User testUser;
    private Profile testProfile;
    private ProfileResponse testProfileResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(Role.STUDENT);

        testProfile = new Profile();
        testProfile.setId(1L);
        testProfile.setUser(testUser);
        testProfile.setBio("Test bio");
        testProfile.setAvatarUrl("avatar.jpg");
        testProfile.setCountry("USA");
        testProfile.setCity("New York");
        testProfile.setCreatedAt(LocalDate.now());
        testProfile.setUpdatedAt(LocalDate.now());

        testProfileResponse = new ProfileResponse();
        testProfileResponse.setId(1L);
        testProfileResponse.setBio("Test bio");
        testProfileResponse.setCountry("USA");
        testProfileResponse.setCity("New York");
        testProfileResponse.setUserId(1L);
        testProfileResponse.setUserName("John Doe");
    }

    @Test
    void createProfile_Success() {
        // Given
        CreateProfileRequest request = new CreateProfileRequest();
        request.setUserId(1L);
        request.setBio("New bio");
        request.setCountry("Canada");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(profileRepository.existsByUserId(1L)).thenReturn(false);
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(testProfileResponse);

        // When
        ProfileResponse response = profileService.createProfile(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test bio", response.getBio());

        verify(userRepository, times(1)).findById(1L);
        verify(profileRepository, times(1)).existsByUserId(1L);
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    void createProfile_UserNotFound() {
        // Given
        CreateProfileRequest request = new CreateProfileRequest();
        request.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            profileService.createProfile(request);
        });
    }

    @Test
    void getProfileByUserId_Success() {
        // Given
        when(profileRepository.findByUserIdWithUser(1L)).thenReturn(Optional.of(testProfile));
        when(profileMapper.toResponse(testProfile)).thenReturn(testProfileResponse);

        // When
        ProfileResponse response = profileService.getProfileByUserId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(profileRepository, times(1)).findByUserIdWithUser(1L);
    }

    @Test
    void updateProfile_Success() {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("Updated bio");
        request.setCity("Los Angeles");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile));
        when(profileRepository.save(any(Profile.class))).thenReturn(testProfile);
        when(profileMapper.toResponse(any(Profile.class))).thenReturn(testProfileResponse);

        // When
        ProfileResponse response = profileService.updateProfile(1L, request);

        // Then
        assertNotNull(response);

        verify(profileRepository, times(1)).findById(1L);
        verify(profileRepository, times(1)).save(testProfile);
    }
}
