package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.QuizResponse;
import ru.skillfactory.learning.platform.entity.Quiz;

@Component
public class QuizMapper {

    public QuizResponse toResponse(Quiz quiz) {
        if (quiz == null) return null;

        QuizResponse response = QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .timeLimit(quiz.getTimeLimit())
                .build();

        if (quiz.getModule() != null) {
            response.setModuleId(quiz.getModule().getId());
            response.setModuleTitle(quiz.getModule().getTitle());
        }

        if (quiz.getQuestions() != null) {
            response.setQuestionCount(quiz.getQuestions().size());
        }

        return response;
    }
}
