package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignmentDetailResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Integer maxScore;
    private Long lessonId;
    private String lessonTitle;
    private Long moduleId;
    private String moduleTitle;
    private Long courseId;
    private String courseTitle;
    private Integer submissionsCount;
    private Integer submittedCount;
    private Integer gradedCount;
}
