package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.UserResponse;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.User;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        if (user.getRole() == Role.TEACHER && user.getCoursesTaught() != null) {
            response.setCourseCount(user.getCoursesTaught().size());
        }

        if (user.getRole() == Role.STUDENT && user.getEnrollments() != null) {
            response.setEnrolledCourses(user.getEnrollments().size());
        }

        return response;
    }
}
