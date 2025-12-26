package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

@Data
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private String videoUrl;
    private Long moduleId;
    private String moduleTitle;
    private Integer assignmentCount;
}