package ru.skillfactory.learning.platform.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QuizResultResponse {
    private Long id;
    private Integer score;
    private Integer absoluteScore;
    private Integer maxScore;
    private LocalDateTime takenAt;
    private Long quizId;
    private String quizTitle;
    private Long studentId;
    private String studentName;
}
