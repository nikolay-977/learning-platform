package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import ru.skillfactory.learning.platform.entity.Role;

import java.time.LocalDate;

@Data
public class ProfileResponse {
    private Long id;
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
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long userId;
    private String userName;
    private String userEmail;
    private Role userRole;
}
