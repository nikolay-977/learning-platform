package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.AssignmentDetailResponse;
import ru.skillfactory.learning.platform.dto.response.AssignmentResponse;
import ru.skillfactory.learning.platform.service.AssignmentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            @Valid @RequestBody CreateAssignmentRequest request) {

        AssignmentResponse assignment = assignmentService.createAssignment(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Assignment created successfully", assignment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> getAssignmentById(@PathVariable Long id) {

        AssignmentResponse assignment = assignmentService.getAssignmentById(id);

        return ResponseEntity.ok(ApiResponse.success(assignment));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<AssignmentDetailResponse>> getAssignmentDetailById(@PathVariable Long id) {

        AssignmentDetailResponse assignment = assignmentService.getAssignmentDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(assignment));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByLesson(
            @PathVariable Long lessonId) {

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByLesson(lessonId);

        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAllAssignments() {

        List<AssignmentResponse> assignments = assignmentService.getAllAssignments();

        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByStudent(
            @PathVariable Long studentId) {

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByStudent(studentId);

        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsByTeacher(
            @PathVariable Long teacherId) {

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsByTeacher(teacherId);

        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @GetMapping("/due-soon")
    public ResponseEntity<ApiResponse<List<AssignmentResponse>>> getAssignmentsDueSoon() {

        List<AssignmentResponse> assignments = assignmentService.getAssignmentsDueSoon();

        return ResponseEntity.ok(ApiResponse.success(assignments));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AssignmentResponse>> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssignmentRequest request) {

        AssignmentResponse assignment = assignmentService.updateAssignment(id, request);

        return ResponseEntity.ok(ApiResponse.success("Assignment updated successfully", assignment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long id) {

        assignmentService.deleteAssignment(id);

        return ResponseEntity.ok(ApiResponse.success("Assignment deleted successfully", null));
    }
}
