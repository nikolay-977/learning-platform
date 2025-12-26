package ru.skillfactory.learning.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAssignmentRequest {
    @NotNull(message = "Assignment ID is required")
    private Long assignmentId;

    @NotBlank(message = "Content is required")
    private String content;
}
