package ru.skillfactory.learning.platform.dto.response;

import lombok.Data;
import ru.skillfactory.learning.platform.entity.QuestionType;

import java.util.List;

@Data
public class QuestionResponse {
    private Long id;
    private String text;
    private QuestionType type;
    private List<AnswerOptionResponse> options;
}