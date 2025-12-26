package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.QuestionResponse;
import ru.skillfactory.learning.platform.entity.Question;

@Component
public class QuestionMapper {

    public QuestionResponse toResponse(Question question) {
        if (question == null) return null;

        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setText(question.getText());
        response.setType(question.getType());

        return response;
    }
}
