package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateQuizRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Module ID is required")
    private Long moduleId;

    private Integer timeLimit;

    private List<CreateQuestionRequest> questions;
}
