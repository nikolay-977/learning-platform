package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxScore;
    private Long lessonId;
    private String lessonTitle;
    private Integer submissionCount;
}
