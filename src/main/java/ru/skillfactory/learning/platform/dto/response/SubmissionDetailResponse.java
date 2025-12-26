package ru.skillfactory.learning.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDetailResponse {
    private Long id;
    private String content;
    private LocalDateTime submittedAt;
    private Integer score;
    private String feedback;
    private Boolean late;

    // Student info
    private Long studentId;
    private String studentName;
    private String studentEmail;

    // Assignment info
    private Long assignmentId;
    private String assignmentTitle;
    private Integer maxScore;
    private LocalDate dueDate;

    // Course info
    private Long courseId;
    private String courseTitle;
}
