package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LessonDetailResponse {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Long moduleId;
    private String moduleTitle;
    private Long courseId;
    private String courseTitle;
    private List<AssignmentResponse> assignments;

    @Data
    public static class AssignmentResponse {
        private Long id;
        private String title;
        private String description;
        private LocalDate dueDate;
        private Integer maxScore;
    }
}
