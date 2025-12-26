package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateCourseRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCourseRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.CourseDetailResponse;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;
import ru.skillfactory.learning.platform.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request) {

        CourseResponse course = courseService.createCourse(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course created successfully", course));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {

        CourseResponse course = courseService.getCourseById(id);

        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseDetailById(@PathVariable Long id) {

        CourseDetailResponse course = courseService.getCourseDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {

        List<CourseResponse> courses = courseService.getAllCourses();

        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByCategory(
            @PathVariable Long categoryId) {

        List<CourseResponse> courses = courseService.getCoursesByCategory(categoryId);

        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByTeacher(
            @PathVariable Long teacherId) {

        List<CourseResponse> courses = courseService.getCoursesByTeacher(teacherId);

        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> searchCourses(
            @RequestParam String keyword) {

        List<CourseResponse> courses = courseService.searchCourses(keyword);

        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/enrolled/{userId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getEnrolledCourses(
            @PathVariable Long userId) {

        List<CourseResponse> courses = courseService.getEnrolledCourses(userId);

        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @GetMapping("/{courseId}/enrolled/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> isUserEnrolled(
            @PathVariable Long courseId,
            @PathVariable Long userId) {

        boolean isEnrolled = courseService.isUserEnrolled(courseId, userId);

        return ResponseEntity.ok(ApiResponse.success(isEnrolled));
    }

    @GetMapping("/{id}/students/count")
    public ResponseEntity<ApiResponse<Integer>> getEnrolledStudentsCount(@PathVariable Long id) {

        int count = courseService.getEnrolledStudentsCount(id);

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {

        CourseResponse course = courseService.updateCourse(id, request);

        return ResponseEntity.ok(ApiResponse.success("Course updated successfully", course));
    }

    @PutMapping("/{courseId}/tags/{tagId}")
    public ResponseEntity<ApiResponse<CourseResponse>> addTagToCourse(
            @PathVariable Long courseId,
            @PathVariable Long tagId) {

        CourseResponse course = courseService.addTagToCourse(courseId, tagId);

        return ResponseEntity.ok(ApiResponse.success("Tag added to course", course));
    }

    @DeleteMapping("/{courseId}/tags/{tagId}")
    public ResponseEntity<ApiResponse<CourseResponse>> removeTagFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long tagId) {

        CourseResponse course = courseService.removeTagFromCourse(courseId, tagId);

        return ResponseEntity.ok(ApiResponse.success("Tag removed from course", course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {

        courseService.deleteCourse(id);

        return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", null));
    }
}
