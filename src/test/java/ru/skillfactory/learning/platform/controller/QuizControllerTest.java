package ru.skillfactory.learning.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skillfactory.learning.platform.dto.request.CreateQuizRequest;
import ru.skillfactory.learning.platform.dto.request.TakeQuizRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateQuizRequest;
import ru.skillfactory.learning.platform.dto.response.*;
import ru.skillfactory.learning.platform.dto.response.QuizDetailResponse.QuestionResponse;
import ru.skillfactory.learning.platform.dto.response.QuizDetailResponse.AnswerOptionResponse;
import ru.skillfactory.learning.platform.entity.QuestionType;
import ru.skillfactory.learning.platform.service.QuizService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class QuizControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizController quizController;

    private QuizResponse quizResponse;
    private QuizDetailResponse quizDetailResponse;
    private QuizResultResponse quizResultResponse;
    private QuestionResponse questionResponse;
    private AnswerOptionResponse answerOptionResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Подготовка тестовых данных для QuizResponse
        quizResponse = QuizResponse.builder()
                .id(1L)
                .title("Основы Java")
                .timeLimit(30)
                .moduleId(101L)
                .moduleTitle("Модуль 1: Основы")
                .questionCount(10)
                .questions(List.of())
                .build();

        // Подготовка тестовых данных для AnswerOptionResponse
        answerOptionResponse = new AnswerOptionResponse();
        answerOptionResponse.setId(1L);
        answerOptionResponse.setText("Вариант ответа A");

        // Подготовка тестовых данных для QuestionResponse
        questionResponse = new QuestionResponse();
        questionResponse.setId(1L);
        questionResponse.setText("Что такое ООП?");
        questionResponse.setType(QuestionType.SINGLE_CHOICE);
        questionResponse.setOptions(List.of(answerOptionResponse));

        // Подготовка тестовых данных для QuizDetailResponse
        quizDetailResponse = QuizDetailResponse.builder()
                .id(1L)
                .title("Основы Java")
                .timeLimit(30)
                .moduleId(101L)
                .moduleTitle("Модуль 1: Основы")
                .courseId(201L)
                .courseTitle("Java для начинающих")
                .questions(List.of(questionResponse))
                .build();

        // Подготовка тестовых данных для QuizResultResponse
        quizResultResponse = QuizResultResponse.builder()
                .id(1L)
                .score(85)
                .absoluteScore(17)
                .maxScore(20)
                .takenAt(LocalDateTime.of(2024, 1, 15, 14, 30, 0))
                .quizId(1L)
                .quizTitle("Основы Java")
                .studentId(1001L)
                .studentName("Иван Иванов")
                .build();
    }

    // Тесты для создания квиза
    @Test
    void createQuiz_ValidRequest_ReturnsCreated() throws Exception {
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("Основы Java")
                .moduleId(101L)
                .timeLimit(30)
                .questions(List.of())
                .build();

        when(quizService.createQuiz(any(CreateQuizRequest.class))).thenReturn(quizResponse);

        mockMvc.perform(post("/api/v1/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Quiz created successfully"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(quizService).createQuiz(any(CreateQuizRequest.class));
    }

    @Test
    void createQuiz_InvalidRequest_MissingTitle_ReturnsBadRequest() throws Exception {
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("")
                .moduleId(101L)
                .build();

        mockMvc.perform(post("/api/v1/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(quizService, never()).createQuiz(any());
    }

    @Test
    void createQuiz_InvalidRequest_MissingModuleId_ReturnsBadRequest() throws Exception {
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("Основы Java")
                .moduleId(null)
                .build();

        mockMvc.perform(post("/api/v1/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(quizService, never()).createQuiz(any());
    }

    // Тесты для получения квиза по ID
    @Test
    void getQuizById_ExistingId_ReturnsOk() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(quizResponse);

        mockMvc.perform(get("/api/v1/quizzes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Основы Java"));

        verify(quizService).getQuizById(1L);
    }

    @Test
    void getQuizDetailById_ExistingId_ReturnsOk() throws Exception {
        when(quizService.getQuizDetailById(1L)).thenReturn(quizDetailResponse);

        mockMvc.perform(get("/api/v1/quizzes/{id}/detail", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Основы Java"));

        verify(quizService).getQuizDetailById(1L);
    }

    // Тесты для получения всех квизов
    @Test
    void getAllQuizzes_ReturnsList() throws Exception {
        List<QuizResponse> quizzes = List.of(quizResponse);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        mockMvc.perform(get("/api/v1/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));

        verify(quizService).getAllQuizzes();
    }

    @Test
    void getQuizzesByCourse_ValidCourseId_ReturnsList() throws Exception {
        List<QuizResponse> quizzes = List.of(quizResponse);
        when(quizService.getQuizzesByCourse(201L)).thenReturn(quizzes);

        mockMvc.perform(get("/api/v1/quizzes/course/{courseId}", 201L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(quizService).getQuizzesByCourse(201L);
    }

    @Test
    void getQuizzesByModule_ValidModuleId_ReturnsList() throws Exception {
        List<QuizResponse> quizzes = List.of(quizResponse);
        when(quizService.getQuizzesByModule(101L)).thenReturn(quizzes);

        mockMvc.perform(get("/api/v1/quizzes/module/{moduleId}", 101L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(quizService).getQuizzesByModule(101L);
    }

    // Тесты для получения результатов
    @Test
    void getQuizResultsByQuiz_ValidQuizId_ReturnsResults() throws Exception {
        List<QuizResultResponse> results = List.of(quizResultResponse);
        when(quizService.getQuizResultsByQuiz(1L)).thenReturn(results);

        mockMvc.perform(get("/api/v1/quizzes/results/quiz/{quizId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].score").value(85));

        verify(quizService).getQuizResultsByQuiz(1L);
    }

    @Test
    void getQuizResultsByStudent_ValidStudentId_ReturnsResults() throws Exception {
        List<QuizResultResponse> results = List.of(quizResultResponse);
        when(quizService.getQuizResultsByStudent(1001L)).thenReturn(results);

        mockMvc.perform(get("/api/v1/quizzes/results/student/{studentId}", 1001L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].studentId").value(1001));

        verify(quizService).getQuizResultsByStudent(1001L);
    }

    @Test
    void getQuizResultById_ValidResultId_ReturnsResult() throws Exception {
        when(quizService.getQuizResultById(1L)).thenReturn(quizResultResponse);

        mockMvc.perform(get("/api/v1/quizzes/results/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.score").value(85));

        verify(quizService).getQuizResultById(1L);
    }

    // Тесты для прохождения квиза
    @Test
    void takeQuiz_ValidRequest_ReturnsCreated() throws Exception {
        TakeQuizRequest request = TakeQuizRequest.builder()
                .quizId(1L)
                .answers(Map.of(1L, 2L))
                .build();

        when(quizService.takeQuiz(eq(1001L), any(TakeQuizRequest.class)))
                .thenReturn(quizResultResponse);

        mockMvc.perform(post("/api/v1/quizzes/student/{studentId}/take", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Quiz completed successfully"));

        verify(quizService).takeQuiz(eq(1001L), any(TakeQuizRequest.class));
    }

    @Test
    void takeQuiz_InvalidRequest_ReturnsBadRequest() throws Exception {
        TakeQuizRequest request = TakeQuizRequest.builder()
                .quizId(null)
                .answers(Map.of(1L, 2L))
                .build();

        mockMvc.perform(post("/api/v1/quizzes/student/{studentId}/take", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(quizService, never()).takeQuiz(anyLong(), any());
    }

    @Test
    void calculateScore_ValidRequest_ReturnsScore() throws Exception {
        Map<Long, Long> answers = Map.of(1L, 2L);

        when(quizService.calculateScore(eq(1L), eq(answers))).thenReturn(85);

        mockMvc.perform(post("/api/v1/quizzes/{id}/calculate-score", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(85));

        verify(quizService).calculateScore(eq(1L), eq(answers));
    }

    // Тесты для обновления квиза
    @Test
    void updateQuiz_ValidRequest_ReturnsOk() throws Exception {
        UpdateQuizRequest request = UpdateQuizRequest.builder()
                .title("Обновленный квиз")
                .timeLimit(45)
                .build();

        QuizResponse updatedResponse = QuizResponse.builder()
                .id(1L)
                .title("Обновленный квиз")
                .timeLimit(45)
                .moduleId(101L)
                .moduleTitle("Модуль 1: Основы")
                .questionCount(10)
                .build();

        when(quizService.updateQuiz(eq(1L), any(UpdateQuizRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/quizzes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Quiz updated successfully"));

        verify(quizService).updateQuiz(eq(1L), any(UpdateQuizRequest.class));
    }

    @Test
    void updateQuiz_InvalidRequest_ReturnsBadRequest() throws Exception {
        UpdateQuizRequest request = UpdateQuizRequest.builder()
                .title("ab")
                .build();

        mockMvc.perform(put("/api/v1/quizzes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(quizService, never()).updateQuiz(anyLong(), any());
    }

    // Тест для удаления квиза
    @Test
    void deleteQuiz_ExistingId_ReturnsOk() throws Exception {
        doNothing().when(quizService).deleteQuiz(1L);

        mockMvc.perform(delete("/api/v1/quizzes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Quiz deleted successfully"));

        verify(quizService).deleteQuiz(1L);
    }

    @Test
    void getAllQuizzes_EmptyList_ReturnsEmptyArray() throws Exception {
        when(quizService.getAllQuizzes()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(quizService).getAllQuizzes();
    }

    @Test
    void calculateScore_EmptyAnswers_ReturnsZero() throws Exception {
        Map<Long, Long> answers = Map.of();

        when(quizService.calculateScore(eq(1L), eq(answers))).thenReturn(0);

        mockMvc.perform(post("/api/v1/quizzes/{id}/calculate-score", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(answers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(0));

        verify(quizService).calculateScore(eq(1L), eq(answers));
    }

    @Test
    void takeQuiz_WithEmptyAnswers_ReturnsCreated() throws Exception {
        TakeQuizRequest request = TakeQuizRequest.builder()
                .quizId(1L)
                .answers(Map.of())
                .build();

        QuizResultResponse resultWithZeroScore = QuizResultResponse.builder()
                .id(1L)
                .score(0)
                .absoluteScore(0)
                .maxScore(20)
                .quizTitle("Основы Java")
                .build();

        when(quizService.takeQuiz(eq(1001L), any(TakeQuizRequest.class)))
                .thenReturn(resultWithZeroScore);

        mockMvc.perform(post("/api/v1/quizzes/student/{studentId}/take", 1001L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.score").value(0));

        verify(quizService).takeQuiz(eq(1001L), any(TakeQuizRequest.class));
    }

    @Test
    void updateQuiz_WithMaxTimeLimit_ReturnsOk() throws Exception {
        UpdateQuizRequest request = UpdateQuizRequest.builder()
                .title("Квиз с макс временем")
                .timeLimit(300) // Максимальное значение
                .build();

        QuizResponse updatedResponse = QuizResponse.builder()
                .id(1L)
                .title("Квиз с макс временем")
                .timeLimit(300)
                .moduleId(101L)
                .moduleTitle("Модуль 1: Основы")
                .questionCount(10)
                .build();

        when(quizService.updateQuiz(eq(1L), any(UpdateQuizRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/quizzes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.timeLimit").value(300));

        verify(quizService).updateQuiz(eq(1L), any(UpdateQuizRequest.class));
    }

    @Test
    void getQuizResultsByQuiz_MultipleResults_ReturnsAll() throws Exception {
        QuizResultResponse result1 = QuizResultResponse.builder()
                .id(1L)
                .score(85)
                .absoluteScore(17)
                .maxScore(20)
                .studentName("Иван Иванов")
                .build();

        QuizResultResponse result2 = QuizResultResponse.builder()
                .id(2L)
                .score(90)
                .absoluteScore(18)
                .maxScore(20)
                .studentName("Петр Петров")
                .build();

        List<QuizResultResponse> results = List.of(result1, result2);
        when(quizService.getQuizResultsByQuiz(1L)).thenReturn(results);

        mockMvc.perform(get("/api/v1/quizzes/results/quiz/{quizId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].studentName").value("Иван Иванов"))
                .andExpect(jsonPath("$.data[1].studentName").value("Петр Петров"));

        verify(quizService).getQuizResultsByQuiz(1L);
    }
}