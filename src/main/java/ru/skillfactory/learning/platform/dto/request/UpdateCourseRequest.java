package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateCourseRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Size(max = 50, message = "Duration cannot exceed 50 characters")
    private String duration;

    private LocalDate startDate;

    private Long categoryId;

    private Long teacherId;
}
