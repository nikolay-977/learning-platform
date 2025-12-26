package ru.skillfactory.learning.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;
    private String content;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
}
