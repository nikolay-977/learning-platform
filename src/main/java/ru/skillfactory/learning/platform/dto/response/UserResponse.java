package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import ru.skillfactory.learning.platform.entity.Role;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private Integer courseCount;
    private Integer enrolledCourses;
}
