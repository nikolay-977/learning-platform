package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;
import ru.skillfactory.learning.platform.entity.Course;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CourseMapper {

    /**
     * Конвертирует список сущностей Course в список CourseResponse
     */
    public List<CourseResponse> toResponseList(List<Course> courses) {
        if (courses == null) {
            return Collections.emptyList();
        }

        return courses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Конвертирует сущность Course в CourseResponse
     */
    public CourseResponse toResponse(Course course) {
        if (course == null) {
            return null;
        }

        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setDuration(course.getDuration());
        response.setStartDate(course.getStartDate());

        // Категория
        if (course.getCategory() != null) {
            response.setCategoryId(course.getCategory().getId());
            response.setCategoryName(course.getCategory().getName());
        }

        // Преподаватель
        if (course.getTeacher() != null) {
            response.setTeacherId(course.getTeacher().getId());
            response.setTeacherName(course.getTeacher().getName());
        }

        // Количество модулей
        if (course.getModules() != null) {
            response.setModuleCount(course.getModules().size());
        }

        // Количество активных записей
        if (course.getEnrollments() != null) {
            long activeEnrollments = course.getEnrollments().stream()
                    .filter(enrollment -> "ACTIVE".equals(enrollment.getStatus()))
                    .count();
            response.setEnrolledStudents((int) activeEnrollments);
        }

        // Средний рейтинг
        if (course.getReviews() != null && !course.getReviews().isEmpty()) {
            double average = course.getReviews().stream()
                    .mapToInt(review -> review.getRating())
                    .average()
                    .orElse(0.0);
            response.setAverageRating(Math.round(average * 10.0) / 10.0);
        }

        return response;
    }
}