package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateLessonRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String content;

    private String videoUrl;

    @NotNull(message = "Module ID is required")
    private Long moduleId;
}
