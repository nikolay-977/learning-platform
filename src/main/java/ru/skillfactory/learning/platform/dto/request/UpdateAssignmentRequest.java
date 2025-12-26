package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateAssignmentRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    private LocalDate dueDate;

    @Min(value = 0, message = "Max score must be at least 0")
    @Max(value = 1000, message = "Max score cannot exceed 1000")
    private Integer maxScore;

    private Long lessonId;
}
