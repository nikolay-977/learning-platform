package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.CreateCourseRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCourseRequest;
import ru.skillfactory.learning.platform.dto.response.CourseDetailResponse;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {

    CourseResponse createCourse(CreateCourseRequest request);

    CourseResponse getCourseById(Long id);

    CourseDetailResponse getCourseDetailById(Long id);

    List<CourseResponse> getAllCourses();

    List<CourseResponse> getCoursesByCategory(Long categoryId);

    List<CourseResponse> getCoursesByTeacher(Long teacherId);

    List<CourseResponse> searchCourses(String keyword);

    CourseResponse updateCourse(Long id, UpdateCourseRequest request);

    void deleteCourse(Long id);

    boolean isUserEnrolled(Long courseId, Long userId);

    List<CourseResponse> getEnrolledCourses(Long userId);

    int getEnrolledStudentsCount(Long courseId);

    CourseResponse addTagToCourse(Long courseId, Long tagId);

    CourseResponse removeTagFromCourse(Long courseId, Long tagId);
}
