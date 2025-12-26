package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import ru.skillfactory.learning.platform.entity.EnrollmentStatus;

import java.time.LocalDate;

@Data
public class EnrollmentResponse {
    private Long id;
    private LocalDate enrollDate;
    private EnrollmentStatus status;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseTitle;
}
