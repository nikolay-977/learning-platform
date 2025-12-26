package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import ru.skillfactory.learning.platform.entity.Role;

import java.time.LocalDateTime;

@Data
public class UserDetailResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private Boolean isActive;

    // Profile info
    private Long profileId;
    private String bio;
    private String avatarUrl;

    // Statistics
    private Integer coursesTaught;
    private Integer enrolledCourses;
    private Integer submissionsCount;
    private Integer quizResultsCount;

    // Last activity
    private LocalDateTime lastLogin;
    private LocalDateTime lastActivity;
}
