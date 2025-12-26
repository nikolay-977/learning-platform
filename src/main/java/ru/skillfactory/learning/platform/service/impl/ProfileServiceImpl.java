package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.skillfactory.learning.platform.service.ProfileService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Override
    @Transactional
    public ProfileResponse createProfile(CreateProfileRequest request) {
        log.info("Creating profile for user ID: {}", request.getUserId());

        // Проверяем, существует ли пользователь
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Проверяем, не существует ли уже профиль для этого пользователя
        if (profileRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("Profile already exists for user ID: " + request.getUserId());
        }

        // Создаем профиль
        Profile profile = new Profile();
        profile.setUser(user);
        updateProfileFields(profile, request);

        Profile savedProfile = profileRepository.save(profile);
        log.info("Profile created with ID: {}", savedProfile.getId());

        return profileMapper.toResponse(savedProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileById(Long id) {
        log.info("Getting profile by ID: {}", id);

        Profile profile = profileRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));

        return profileMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getProfileByUserId(Long userId) {
        log.info("Getting profile by user ID: {}", userId);

        Profile profile = profileRepository.findByUserIdWithUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "user ID", userId));

        return profileMapper.toResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileResponse> getAllProfiles() {
        log.info("Getting all profiles");

        List<Profile> profiles = profileRepository.findAll();

        return profiles.stream()
                .map(profile -> {
                    // Явно загружаем пользователя для каждого профиля
                    if (profile.getUser() != null && profile.getUser().getId() != null) {
                        profile.setUser(userRepository.findById(profile.getUser().getId()).orElse(null));
                    }
                    return profileMapper.toResponse(profile);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(Long id, UpdateProfileRequest request) {
        log.info("Updating profile with ID: {}", id);

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", id));

        updateProfileFields(profile, request);

        Profile updatedProfile = profileRepository.save(profile);
        log.info("Profile updated with ID: {}", updatedProfile.getId());

        return profileMapper.toResponse(updatedProfile);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfileByUserId(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "user ID", userId));

        updateProfileFields(profile, request);

        Profile updatedProfile = profileRepository.save(profile);
        log.info("Profile updated for user ID: {}", userId);

        return profileMapper.toResponse(updatedProfile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        log.info("Deleting profile with ID: {}", id);

        if (!profileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profile", "id", id);
        }

        profileRepository.deleteById(id);
        log.info("Profile deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteProfileByUserId(Long userId) {
        log.info("Deleting profile for user ID: {}", userId);

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "user ID", userId));

        profileRepository.delete(profile);
        log.info("Profile deleted for user ID: {}", userId);
    }

    private void updateProfileFields(Profile profile, CreateProfileRequest request) {
        profile.setBio(request.getBio());
        profile.setAvatarUrl(request.getAvatarUrl());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setCountry(request.getCountry());
        profile.setCity(request.getCity());
        profile.setBirthDate(request.getBirthDate());
        profile.setEducation(request.getEducation());
        profile.setWorkExperience(request.getWorkExperience());
        profile.setSkills(request.getSkills());
        profile.setWebsiteUrl(request.getWebsiteUrl());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());
    }

    private void updateProfileFields(Profile profile, UpdateProfileRequest request) {
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getBirthDate() != null) {
            profile.setBirthDate(request.getBirthDate());
        }
        if (request.getEducation() != null) {
            profile.setEducation(request.getEducation());
        }
        if (request.getWorkExperience() != null) {
            profile.setWorkExperience(request.getWorkExperience());
        }
        if (request.getSkills() != null) {
            profile.setSkills(request.getSkills());
        }
        if (request.getWebsiteUrl() != null) {
            profile.setWebsiteUrl(request.getWebsiteUrl());
        }
        if (request.getLinkedinUrl() != null) {
            profile.setLinkedinUrl(request.getLinkedinUrl());
        }
        if (request.getGithubUrl() != null) {
            profile.setGithubUrl(request.getGithubUrl());
        }
    }
}