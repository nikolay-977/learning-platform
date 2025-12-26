package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateQuizRequest;
import ru.skillfactory.learning.platform.dto.request.TakeQuizRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateQuizRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.QuizDetailResponse;
import ru.skillfactory.learning.platform.dto.response.QuizResponse;
import ru.skillfactory.learning.platform.dto.response.QuizResultResponse;
import ru.skillfactory.learning.platform.service.QuizService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(
            @Valid @RequestBody CreateQuizRequest request) {

        QuizResponse quiz = quizService.createQuiz(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Quiz created successfully", quiz));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizById(@PathVariable Long id) {

        QuizResponse quiz = quizService.getQuizById(id);

        return ResponseEntity.ok(ApiResponse.success(quiz));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<QuizDetailResponse>> getQuizDetailById(@PathVariable Long id) {

        QuizDetailResponse quiz = quizService.getQuizDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(quiz));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getAllQuizzes() {

        List<QuizResponse> quizzes = quizService.getAllQuizzes();

        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getQuizzesByCourse(
            @PathVariable Long courseId) {

        List<QuizResponse> quizzes = quizService.getQuizzesByCourse(courseId);

        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getQuizzesByModule(
            @PathVariable Long moduleId) {

        List<QuizResponse> quizzes = quizService.getQuizzesByModule(moduleId);

        return ResponseEntity.ok(ApiResponse.success(quizzes));
    }

    @GetMapping("/results/quiz/{quizId}")
    public ResponseEntity<ApiResponse<List<QuizResultResponse>>> getQuizResultsByQuiz(
            @PathVariable Long quizId) {

        List<QuizResultResponse> results = quizService.getQuizResultsByQuiz(quizId);

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/results/student/{studentId}")
    public ResponseEntity<ApiResponse<List<QuizResultResponse>>> getQuizResultsByStudent(
            @PathVariable Long studentId) {

        List<QuizResultResponse> results = quizService.getQuizResultsByStudent(studentId);

        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<ApiResponse<QuizResultResponse>> getQuizResultById(@PathVariable Long id) {

        QuizResultResponse result = quizService.getQuizResultById(id);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/student/{studentId}/take")
    public ResponseEntity<ApiResponse<QuizResultResponse>> takeQuiz(
            @PathVariable Long studentId,
            @Valid @RequestBody TakeQuizRequest request) {

        QuizResultResponse result = quizService.takeQuiz(studentId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Quiz completed successfully", result));
    }

    @PostMapping("/{id}/calculate-score")
    public ResponseEntity<ApiResponse<Integer>> calculateScore(
            @PathVariable Long id,
            @RequestBody Map<Long, Long> answers) {

        int score = quizService.calculateScore(id, answers);

        return ResponseEntity.ok(ApiResponse.success(score));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuizRequest request) {

        QuizResponse quiz = quizService.updateQuiz(id, request);

        return ResponseEntity.ok(ApiResponse.success("Quiz updated successfully", quiz));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable Long id) {

        quizService.deleteQuiz(id);

        return ResponseEntity.ok(ApiResponse.success("Quiz deleted successfully", null));
    }
}
