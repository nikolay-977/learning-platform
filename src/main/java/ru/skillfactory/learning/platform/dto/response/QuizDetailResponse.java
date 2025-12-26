package ru.skillfactory.learning.platform.dto.response;

import lombok.Builder;
import lombok.Data;
import ru.skillfactory.learning.platform.entity.QuestionType;

import java.util.List;

@Data
@Builder
public class QuizDetailResponse {
    private Long id;
    private String title;
    private Integer timeLimit;
    private Long moduleId;
    private String moduleTitle;
    private Long courseId;
    private String courseTitle;
    private List<QuestionResponse> questions;

    @Data
    public static class QuestionResponse {
        private Long id;
        private String text;
        private QuestionType type;
        private List<AnswerOptionResponse> options;
    }

    @Data
    public static class AnswerOptionResponse {
        private Long id;
        private String text;
    }
}
