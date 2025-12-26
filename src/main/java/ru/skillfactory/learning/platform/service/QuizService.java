package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.CreateQuizRequest;
import ru.skillfactory.learning.platform.dto.request.TakeQuizRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateQuizRequest;
import ru.skillfactory.learning.platform.dto.response.QuizDetailResponse;
import ru.skillfactory.learning.platform.dto.response.QuizResponse;
import ru.skillfactory.learning.platform.dto.response.QuizResultResponse;

import java.util.List;

public interface QuizService {

    QuizResponse createQuiz(CreateQuizRequest request);

    QuizResponse getQuizById(Long id);

    QuizDetailResponse getQuizDetailById(Long id);

    List<QuizResponse> getAllQuizzes();

    QuizResponse updateQuiz(Long id, UpdateQuizRequest request);

    void deleteQuiz(Long id);

    QuizResultResponse takeQuiz(Long studentId, TakeQuizRequest request);

    List<QuizResultResponse> getQuizResultsByQuiz(Long quizId);

    List<QuizResultResponse> getQuizResultsByStudent(Long studentId);

    QuizResultResponse getQuizResultById(Long id);

    List<QuizResponse> getQuizzesByCourse(Long courseId);

    List<QuizResponse> getQuizzesByModule(Long moduleId);

    int calculateScore(Long quizId, java.util.Map<Long, Long> answers);
}
