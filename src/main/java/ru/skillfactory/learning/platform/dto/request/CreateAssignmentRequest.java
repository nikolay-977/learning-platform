package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAssignmentRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    private LocalDate dueDate;

    @NotNull(message = "Max score is required")
    private Integer maxScore;
}
