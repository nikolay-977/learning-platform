package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.CreateProfileRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateProfileRequest;
import ru.skillfactory.learning.platform.dto.response.ProfileResponse;

import java.util.List;

public interface ProfileService {

    ProfileResponse createProfile(CreateProfileRequest request);

    ProfileResponse getProfileById(Long id);

    ProfileResponse getProfileByUserId(Long userId);

    List<ProfileResponse> getAllProfiles();

    ProfileResponse updateProfile(Long id, UpdateProfileRequest request);

    ProfileResponse updateProfileByUserId(Long userId, UpdateProfileRequest request);

    void deleteProfile(Long id);

    void deleteProfileByUserId(Long userId);
}
