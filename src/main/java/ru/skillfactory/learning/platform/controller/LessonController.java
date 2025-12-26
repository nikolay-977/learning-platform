package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateLessonRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateLessonRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.LessonDetailResponse;
import ru.skillfactory.learning.platform.dto.response.LessonResponse;
import ru.skillfactory.learning.platform.service.LessonService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @Valid @RequestBody CreateLessonRequest request) {

        LessonResponse lesson = lessonService.createLesson(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lesson created successfully", lesson));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable Long id) {

        LessonResponse lesson = lessonService.getLessonById(id);

        return ResponseEntity.ok(ApiResponse.success(lesson));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<LessonDetailResponse>> getLessonDetailById(@PathVariable Long id) {

        LessonDetailResponse lesson = lessonService.getLessonDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(lesson));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByModule(
            @PathVariable Long moduleId) {

        List<LessonResponse> lessons = lessonService.getLessonsByModule(moduleId);

        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getAllLessons() {

        List<LessonResponse> lessons = lessonService.getAllLessons();

        return ResponseEntity.ok(ApiResponse.success(lessons));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLessonRequest request) {

        LessonResponse lesson = lessonService.updateLesson(id, request);

        return ResponseEntity.ok(ApiResponse.success("Lesson updated successfully", lesson));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Long id) {

        lessonService.deleteLesson(id);

        return ResponseEntity.ok(ApiResponse.success("Lesson deleted successfully", null));
    }
}
