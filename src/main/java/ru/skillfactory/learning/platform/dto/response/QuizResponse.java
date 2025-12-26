package ru.skillfactory.learning.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private Integer timeLimit;
    private Long moduleId;
    private String moduleTitle;
    private Integer questionCount;
    private List<QuestionResponse> questions;
}