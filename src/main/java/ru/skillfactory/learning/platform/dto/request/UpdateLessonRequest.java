package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLessonRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content;

    private String videoUrl;

    private Long moduleId;
}
