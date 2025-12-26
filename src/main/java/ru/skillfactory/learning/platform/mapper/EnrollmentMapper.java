package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.EnrollmentResponse;
import ru.skillfactory.learning.platform.entity.Enrollment;

@Component
public class EnrollmentMapper {

    public EnrollmentResponse toResponse(Enrollment enrollment) {
        if (enrollment == null) return null;

        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setEnrollDate(enrollment.getEnrollDate());
        response.setStatus(enrollment.getStatus());

        if (enrollment.getStudent() != null) {
            response.setStudentId(enrollment.getStudent().getId());
            response.setStudentName(enrollment.getStudent().getName());
        }

        if (enrollment.getCourse() != null) {
            response.setCourseId(enrollment.getCourse().getId());
            response.setCourseTitle(enrollment.getCourse().getTitle());
        }

        return response;
    }
}
