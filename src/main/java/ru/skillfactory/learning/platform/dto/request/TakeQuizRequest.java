package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TakeQuizRequest {
    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotNull(message = "Answers are required")
    private Map<Long, Long> answers; // questionId -> selectedOptionId
}