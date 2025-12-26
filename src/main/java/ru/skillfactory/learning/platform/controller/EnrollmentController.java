package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.EnrollRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.EnrollmentResponse;
import ru.skillfactory.learning.platform.service.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollStudent(
            @Valid @RequestBody EnrollRequest request) {

        EnrollmentResponse enrollment = enrollmentService.enrollStudent(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student enrolled successfully", enrollment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentById(@PathVariable Long id) {

        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(id);

        return ResponseEntity.ok(ApiResponse.success(enrollment));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByStudent(
            @PathVariable Long studentId) {

        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);

        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByCourse(
            @PathVariable Long courseId) {

        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);

        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAllEnrollments() {

        List<EnrollmentResponse> enrollments = enrollmentService.getAllEnrollments();

        return ResponseEntity.ok(ApiResponse.success(enrollments));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<ApiResponse<Boolean>> isStudentEnrolled(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        boolean isEnrolled = enrollmentService.isStudentEnrolled(studentId, courseId);

        return ResponseEntity.ok(ApiResponse.success(isEnrolled));
    }

    @GetMapping("/course/{courseId}/active/count")
    public ResponseEntity<ApiResponse<Integer>> getActiveEnrollmentsCount(
            @PathVariable Long courseId) {

        int count = enrollmentService.getActiveEnrollmentsCount(courseId);

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateEnrollmentStatus(
            @PathVariable Long id,
            @PathVariable String status) {

        EnrollmentResponse enrollment = enrollmentService.updateEnrollmentStatus(id, status);

        return ResponseEntity.ok(ApiResponse.success("Enrollment status updated", enrollment));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> completeCourse(@PathVariable Long id) {

        EnrollmentResponse enrollment = enrollmentService.completeCourse(id);

        return ResponseEntity.ok(ApiResponse.success("Course marked as completed", enrollment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollment(@PathVariable Long id) {

        enrollmentService.cancelEnrollment(id);

        return ResponseEntity.ok(ApiResponse.success("Enrollment canceled", null));
    }

    @DeleteMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<ApiResponse<Void>> cancelEnrollmentByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        enrollmentService.cancelEnrollmentByStudentAndCourse(studentId, courseId);

        return ResponseEntity.ok(ApiResponse.success("Enrollment canceled", null));
    }
}