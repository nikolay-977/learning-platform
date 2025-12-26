package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.ProfileResponse;
import ru.skillfactory.learning.platform.entity.Profile;

@Component
public class ProfileMapper {

    public ProfileResponse toResponse(Profile profile) {
        if (profile == null) return null;

        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setBio(profile.getBio());
        response.setAvatarUrl(profile.getAvatarUrl());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setCountry(profile.getCountry());
        response.setCity(profile.getCity());
        response.setBirthDate(profile.getBirthDate());
        response.setEducation(profile.getEducation());
        response.setWorkExperience(profile.getWorkExperience());
        response.setSkills(profile.getSkills());
        response.setWebsiteUrl(profile.getWebsiteUrl());
        response.setLinkedinUrl(profile.getLinkedinUrl());
        response.setGithubUrl(profile.getGithubUrl());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());

        if (profile.getUser() != null) {
            response.setUserId(profile.getUser().getId());
            response.setUserName(profile.getUser().getName());
            response.setUserEmail(profile.getUser().getEmail());
            response.setUserRole(profile.getUser().getRole());
        }

        return response;
    }
}
