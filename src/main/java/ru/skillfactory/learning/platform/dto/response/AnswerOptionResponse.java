package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;

@Data
public class AnswerOptionResponse {
    private Long id;
    private String text;
    private Boolean isCorrect;
}
