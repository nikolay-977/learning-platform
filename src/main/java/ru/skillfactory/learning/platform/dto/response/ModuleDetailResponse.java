package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ModuleDetailResponse {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Long courseId;
    private String courseTitle;
    private List<LessonResponse> lessons;
    private Boolean hasQuiz;
    private Long quizId;
    private String quizTitle;

    @Data
    public static class LessonResponse {
        private Long id;
        private String title;
        private String content;
        private String videoUrl;
    }
}
