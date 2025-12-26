package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerOptionRequest {
    @NotBlank(message = "Answer text is required")
    private String text;

    @NotNull(message = "isCorrect flag is required")
    private Boolean isCorrect;
}
