package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseDetailResponse {
    private Long id;
    private String title;
    private String description;
    private String duration;
    private LocalDate startDate;
    private Long categoryId;
    private String categoryName;
    private Long teacherId;
    private String teacherName;
    private Double averageRating;
    private List<ModuleResponse> modules;
    private List<String> tags;
    private List<ReviewResponse> reviews;

    @Data
    public static class ModuleResponse {
        private Long id;
        private String title;
        private String description;
        private Integer orderIndex;
        private List<LessonResponse> lessons;
    }

    @Data
    public static class LessonResponse {
        private Long id;
        private String title;
        private String content;
        private String videoUrl;
    }

    @Data
    public static class ReviewResponse {
        private Long id;
        private Integer rating;
        private String comment;
        private String studentName;
        private LocalDateTime createdAt;
    }
}
