package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.SubmitAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateSubmissionRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.SubmissionDetailResponse;
import ru.skillfactory.learning.platform.dto.response.SubmissionResponse;
import ru.skillfactory.learning.platform.service.SubmissionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitAssignment(
            @PathVariable Long studentId,
            @Valid @RequestBody SubmitAssignmentRequest request) {

        SubmissionResponse submission = submissionService.submitAssignment(studentId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assignment submitted successfully", submission));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(@PathVariable Long id) {

        SubmissionResponse submission = submissionService.getSubmissionById(id);

        return ResponseEntity.ok(ApiResponse.success(submission));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetailById(@PathVariable Long id) {

        SubmissionDetailResponse submission = submissionService.getSubmissionDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(submission));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissionsByAssignment(
            @PathVariable Long assignmentId) {

        List<SubmissionResponse> submissions = submissionService.getSubmissionsByAssignment(assignmentId);

        return ResponseEntity.ok(ApiResponse.success(submissions));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissionsByStudent(
            @PathVariable Long studentId) {

        List<SubmissionResponse> submissions = submissionService.getSubmissionsByStudent(studentId);

        return ResponseEntity.ok(ApiResponse.success(submissions));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getAllSubmissions() {

        List<SubmissionResponse> submissions = submissionService.getAllSubmissions();

        return ResponseEntity.ok(ApiResponse.success(submissions));
    }

    @GetMapping("/ungraded")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getUngradedSubmissions() {

        List<SubmissionResponse> submissions = submissionService.getUngradedSubmissions();

        return ResponseEntity.ok(ApiResponse.success(submissions));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissionsByCourse(
            @PathVariable Long courseId) {

        List<SubmissionResponse> submissions = submissionService.getSubmissionsByCourse(courseId);

        return ResponseEntity.ok(ApiResponse.success(submissions));
    }

    @GetMapping("/student/{studentId}/assignment/{assignmentId}")
    public ResponseEntity<ApiResponse<Boolean>> hasStudentSubmitted(
            @PathVariable Long studentId,
            @PathVariable Long assignmentId) {

        boolean hasSubmitted = submissionService.hasStudentSubmitted(studentId, assignmentId);

        return ResponseEntity.ok(ApiResponse.success(hasSubmitted));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SubmissionResponse>> updateSubmission(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSubmissionRequest request) {

        SubmissionResponse submission = submissionService.updateSubmission(id, request);

        return ResponseEntity.ok(ApiResponse.success("Submission updated successfully", submission));
    }

    @PutMapping("/{id}/grade")
    public ResponseEntity<ApiResponse<SubmissionResponse>> gradeSubmission(
            @PathVariable Long id,
            @RequestParam Integer score,
            @RequestParam(required = false) String feedback) {

        SubmissionResponse submission = submissionService.gradeSubmission(id, score, feedback);

        return ResponseEntity.ok(ApiResponse.success("Submission graded", submission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSubmission(@PathVariable Long id) {

        submissionService.deleteSubmission(id);

        return ResponseEntity.ok(ApiResponse.success("Submission deleted successfully", null));
    }
}
