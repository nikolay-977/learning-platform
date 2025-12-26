package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

@Data
public class ModuleResponse {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Long courseId;
    private String courseTitle;
    private Integer lessonCount;
    private Boolean hasQuiz;
}
