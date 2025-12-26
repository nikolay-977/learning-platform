package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.EnrollRequest;
import ru.skillfactory.learning.platform.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enrollStudent(EnrollRequest request);

    EnrollmentResponse getEnrollmentById(Long id);

    List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId);

    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);

    List<EnrollmentResponse> getAllEnrollments();

    EnrollmentResponse updateEnrollmentStatus(Long id, String status);

    void cancelEnrollment(Long id);

    void cancelEnrollmentByStudentAndCourse(Long studentId, Long courseId);

    boolean isStudentEnrolled(Long studentId, Long courseId);

    int getActiveEnrollmentsCount(Long courseId);

    EnrollmentResponse completeCourse(Long enrollmentId);
}
