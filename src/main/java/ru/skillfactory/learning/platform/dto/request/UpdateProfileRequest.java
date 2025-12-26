package ru.skillfactory.learning.platform.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String bio;
    private String avatarUrl;
    private String phoneNumber;
    private String country;
    private String city;
    private LocalDate birthDate;
    private String education;
    private String workExperience;
    private String skills;
    private String websiteUrl;
    private String linkedinUrl;
    private String githubUrl;
}
