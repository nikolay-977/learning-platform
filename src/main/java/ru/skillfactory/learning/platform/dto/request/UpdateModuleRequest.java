package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateModuleRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private Integer orderIndex;
}
