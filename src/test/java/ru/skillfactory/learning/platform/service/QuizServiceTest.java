package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.CreateQuizRequest;
import ru.skillfactory.learning.platform.dto.request.TakeQuizRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateQuizRequest;
import ru.skillfactory.learning.platform.dto.response.QuizResponse;
import ru.skillfactory.learning.platform.dto.response.QuizResultResponse;
import ru.skillfactory.learning.platform.entity.*;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.mapper.QuizMapper;
import ru.skillfactory.learning.platform.repository.*;
import ru.skillfactory.learning.platform.service.impl.QuizServiceImpl;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerOptionRepository answerOptionRepository;

    @Mock
    private QuizSubmissionRepository quizSubmissionRepository;

    @Mock
    private QuizMapper quizMapper;

    @InjectMocks
    private QuizServiceImpl quizService;

    private Module testModule;
    private User testStudent;
    private Quiz testQuiz;
    private Question testQuestion;
    private AnswerOption correctOption;
    private AnswerOption wrongOption;
    private QuizResponse testQuizResponse;
    private QuizResultResponse testQuizResultResponse;

    @BeforeEach
    void setUp() {
        // Создаем тестовый модуль с билдером
        testModule = Module.builder()
                .id(1L)
                .title("Module 1")
                .course(null)
                .orderIndex(1)
                .build();

        // Создаем тестового студента с билдером
        testStudent = User.builder()
                .id(1L)
                .name("John Student")
                .email("student@example.com")
                .role(Role.STUDENT)
                .build();

        // Создаем тестовый квиз с билдером
        testQuiz = Quiz.builder()
                .id(1L)
                .title("Quiz 1")
                .timeLimit(30)
                .module(testModule)
                .build();

        // Создаем правильный вариант ответа с билдером
        correctOption = AnswerOption.builder()
                .id(1L)
                .text("4")
                .isCorrect(true)
                .build();

        // Создаем неправильный вариант ответа с билдером
        wrongOption = AnswerOption.builder()
                .id(2L)
                .text("5")
                .isCorrect(false)
                .build();

        // Создаем тестовый вопрос с билдером
        testQuestion = Question.builder()
                .id(1L)
                .text("What is 2+2?")
                .type(QuestionType.SINGLE_CHOICE)
                .quiz(testQuiz)
                .options(Arrays.asList(correctOption, wrongOption))
                .build();

        // Устанавливаем обратные ссылки
        correctOption.setQuestion(testQuestion);
        wrongOption.setQuestion(testQuestion);
        testQuiz.setQuestions(Arrays.asList(testQuestion));
        testModule.setQuiz(testQuiz);

        // Создаем тестовый QuizResponse с билдером
        testQuizResponse = QuizResponse.builder()
                .id(1L)
                .title("Quiz 1")
                .timeLimit(30)
                .moduleId(1L)
                .moduleTitle("Module 1")
                .questionCount(1)
                .questions(List.of())
                .build();

        // Создаем тестовый QuizResultResponse с билдером
        testQuizResultResponse = QuizResultResponse.builder()
                .id(1L)
                .score(100)
                .absoluteScore(1)
                .maxScore(1)
                .takenAt(LocalDateTime.now())
                .quizId(1L)
                .quizTitle("Quiz 1")
                .studentId(1L)
                .studentName("John Student")
                .build();
    }

    @Test
    void createQuiz_Success() {
        // Given
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("Quiz 1")
                .moduleId(1L)
                .timeLimit(30)
                .questions(List.of())
                .build();

        // Создаем модуль без квиза для этого теста
        Module moduleWithoutQuiz = Module.builder()
                .id(1L)
                .title("Module 1")
                .build();

        Quiz savedQuiz = Quiz.builder()
                .id(1L)
                .title("Quiz 1")
                .timeLimit(30)
                .module(moduleWithoutQuiz)
                .build();

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(moduleWithoutQuiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);
        when(quizMapper.toResponse(any(Quiz.class))).thenReturn(testQuizResponse);

        // When
        QuizResponse response = quizService.createQuiz(request);

        // Then
        assertNotNull(response);
        assertEquals("Quiz 1", response.getTitle());
        assertEquals(30, response.getTimeLimit());

        verify(moduleRepository).findById(1L);
        verify(quizRepository).save(any(Quiz.class));
        verify(quizMapper).toResponse(any(Quiz.class));
    }

    @Test
    void createQuiz_ModuleAlreadyHasQuiz_ThrowsException() {
        // Given
        CreateQuizRequest request = CreateQuizRequest.builder()
                .title("Quiz 1")
                .moduleId(1L)
                .build();

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            quizService.createQuiz(request);
        });

        verify(moduleRepository).findById(1L);
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void takeQuiz_Success() {
        // Given
        TakeQuizRequest request = TakeQuizRequest.builder()
                .quizId(1L)
                .answers(Map.of(1L, 1L)) // Выбран правильный ответ
                .build();

        QuizSubmission quizSubmission = QuizSubmission.builder()
                .id(1L)
                .student(testStudent)
                .quiz(testQuiz)
                .score(100)
                .takenAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizRepository.findByIdWithQuestions(1L)).thenReturn(Optional.of(testQuiz));
        when(quizSubmissionRepository.findByQuizIdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenReturn(quizSubmission);

        // When
        QuizResultResponse result = quizService.takeQuiz(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(100, result.getScore());
        assertEquals(1, result.getAbsoluteScore());

        verify(userRepository).findById(1L);
        verify(quizRepository).findById(1L);
        verify(quizRepository).findByIdWithQuestions(1L);
        verify(quizSubmissionRepository).findByQuizIdAndStudentId(1L, 1L);
        verify(quizSubmissionRepository).save(any(QuizSubmission.class));

    }

    @Test
    void takeQuiz_AlreadyTaken_ThrowsException() {
        // Given
        TakeQuizRequest request = TakeQuizRequest.builder()
                .quizId(1L)
                .answers(Map.of(1L, 1L))
                .build();

        QuizSubmission existingSubmission = QuizSubmission.builder()
                .id(1L)
                .student(testStudent)
                .quiz(testQuiz)
                .score(80)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizSubmissionRepository.findByQuizIdAndStudentId(1L, 1L))
                .thenReturn(Optional.of(existingSubmission));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            quizService.takeQuiz(1L, request);
        });

        verify(quizSubmissionRepository).findByQuizIdAndStudentId(1L, 1L);
        verify(quizSubmissionRepository, never()).save(any(QuizSubmission.class));
    }

    @Test
    void calculateScore_AllCorrect() {
        // Given
        Map<Long, Long> answers = Map.of(1L, 1L); // Выбран правильный ответ

        when(quizRepository.findByIdWithQuestions(1L)).thenReturn(Optional.of(testQuiz));

        // When
        int score = quizService.calculateScore(1L, answers);

        // Then
        assertEquals(1, score); // 1 правильный ответ из 1

        verify(quizRepository).findByIdWithQuestions(1L);
    }

    @Test
    void calculateScore_WrongAnswer() {
        // Given
        Map<Long, Long> answers = Map.of(1L, 2L); // Выбран неправильный ответ

        when(quizRepository.findByIdWithQuestions(1L)).thenReturn(Optional.of(testQuiz));

        // When
        int score = quizService.calculateScore(1L, answers);

        // Then
        assertEquals(0, score); // 0 правильных ответов

        verify(quizRepository).findByIdWithQuestions(1L);
    }

    @Test
    void calculateScore_NoAnswer() {
        // Given
        Map<Long, Long> answers = new HashMap<>(); // Нет ответов

        when(quizRepository.findByIdWithQuestions(1L)).thenReturn(Optional.of(testQuiz));

        // When
        int score = quizService.calculateScore(1L, answers);

        // Then
        assertEquals(0, score); // 0 правильных ответов

        verify(quizRepository).findByIdWithQuestions(1L);
    }

    @Test
    void calculateScore_MultipleQuestions() {
        // Given
        // Создаем второй вопрос
        Question question2 = Question.builder()
                .id(2L)
                .text("What is 3+3?")
                .type(QuestionType.SINGLE_CHOICE)
                .quiz(testQuiz)
                .build();

        AnswerOption correctOption2 = AnswerOption.builder()
                .id(3L)
                .text("6")
                .isCorrect(true)
                .question(question2)
                .build();

        AnswerOption wrongOption2 = AnswerOption.builder()
                .id(4L)
                .text("7")
                .isCorrect(false)
                .question(question2)
                .build();

        question2.setOptions(Arrays.asList(correctOption2, wrongOption2));
        testQuiz.setQuestions(Arrays.asList(testQuestion, question2));

        Map<Long, Long> answers = Map.of(
                1L, 1L, // Правильный ответ на первый вопрос
                2L, 4L  // Неправильный ответ на второй вопрос
        );

        when(quizRepository.findByIdWithQuestions(1L)).thenReturn(Optional.of(testQuiz));

        // When
        int score = quizService.calculateScore(1L, answers);

        // Then
        assertEquals(1, score); // 1 правильный ответ из 2

        verify(quizRepository).findByIdWithQuestions(1L);
    }

    @Test
    void getQuizResultsByStudent_Success() {
        // Given
        QuizSubmission quizSubmission = QuizSubmission.builder()
                .id(1L)
                .student(testStudent)
                .quiz(testQuiz)
                .score(100)
                .takenAt(LocalDateTime.now())
                .build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(quizSubmissionRepository.findByStudentId(1L)).thenReturn(List.of(quizSubmission));

        // When
        List<QuizResultResponse> results = quizService.getQuizResultsByStudent(1L);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(100, results.get(0).getScore());

        verify(userRepository).existsById(1L);
        verify(quizSubmissionRepository).findByStudentId(1L);
    }

    @Test
    void getQuizResultsByStudent_NoResults_ReturnsEmptyList() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(quizSubmissionRepository.findByStudentId(1L)).thenReturn(List.of());

        // When
        List<QuizResultResponse> results = quizService.getQuizResultsByStudent(1L);

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());

        verify(userRepository).existsById(1L);
        verify(quizSubmissionRepository).findByStudentId(1L);
    }

    @Test
    void getQuizById_Success() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // When
        QuizResponse response = quizService.getQuizById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Quiz 1", response.getTitle());

        verify(quizRepository).findById(1L);
        verify(quizMapper).toResponse(testQuiz);
    }

    @Test
    void deleteQuiz_Success() {
        // Given
        when(quizRepository.existsById(1L)).thenReturn(true);

        // When
        quizService.deleteQuiz(1L);

        // Then
        verify(quizRepository).existsById(1L);
        verify(quizRepository).deleteById(1L);
    }

    @Test
    void getAllQuizzes_Success() {
        // Given
        List<Quiz> quizzes = List.of(testQuiz);
        when(quizRepository.findAll()).thenReturn(quizzes);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // When
        List<QuizResponse> results = quizService.getAllQuizzes();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Quiz 1", results.get(0).getTitle());

        verify(quizRepository).findAll();
        verify(quizMapper).toResponse(testQuiz);
    }

    @Test
    void getQuizDetailById_Success() {
        // Given
        when(quizRepository.findByIdWithQuestions(1L)).thenReturn(Optional.of(testQuiz));

        // When
        var result = quizService.getQuizDetailById(1L);

        // Then
        assertNotNull(result);
        verify(quizRepository).findByIdWithQuestions(1L);
    }

    @Test
    void getQuizResultsByQuiz_Success() {
        // Given
        QuizSubmission quizSubmission = QuizSubmission.builder()
                .id(1L)
                .student(testStudent)
                .quiz(testQuiz)
                .score(100)
                .build();

        when(quizRepository.existsById(1L)).thenReturn(true);
        when(quizSubmissionRepository.findByQuizId(1L)).thenReturn(List.of(quizSubmission));

        // When
        List<QuizResultResponse> results = quizService.getQuizResultsByQuiz(1L);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());

        verify(quizRepository).existsById(1L);
        verify(quizSubmissionRepository).findByQuizId(1L);
    }

    @Test
    void updateQuiz_Success() {
        // Given
        var updateRequest = UpdateQuizRequest.builder()
                .title("Updated Quiz")
                .timeLimit(45)
                .build();


        Quiz updatedQuiz = Quiz.builder()
                .id(1L)
                .title("Updated Quiz")
                .timeLimit(45)
                .module(testModule)
                .build();

        QuizResponse updatedResponse = QuizResponse.builder()
                .id(1L)
                .title("Updated Quiz")
                .timeLimit(45)
                .moduleId(1L)
                .moduleTitle("Module 1")
                .build();

        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(updatedQuiz);
        when(quizMapper.toResponse(any(Quiz.class))).thenReturn(updatedResponse);

        // When
        QuizResponse result = quizService.updateQuiz(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals("Updated Quiz", result.getTitle());
        assertEquals(45, result.getTimeLimit());

        verify(quizRepository).findById(1L);
        verify(quizRepository).save(any(Quiz.class));
        verify(quizMapper).toResponse(any(Quiz.class));
    }
}