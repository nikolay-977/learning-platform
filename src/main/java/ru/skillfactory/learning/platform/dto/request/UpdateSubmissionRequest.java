package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSubmissionRequest {

    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content;

    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 1000, message = "Score cannot exceed 1000")
    private Integer score;

    @Size(max = 1000, message = "Feedback cannot exceed 1000 characters")
    private String feedback;
}
