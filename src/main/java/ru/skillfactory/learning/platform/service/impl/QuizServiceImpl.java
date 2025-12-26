package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.*;
import ru.skillfactory.learning.platform.dto.response.QuizDetailResponse;
import ru.skillfactory.learning.platform.dto.response.QuizResponse;
import ru.skillfactory.learning.platform.dto.response.QuizResultResponse;
import ru.skillfactory.learning.platform.entity.*;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.QuizMapper;
import ru.skillfactory.learning.platform.repository.*;
import ru.skillfactory.learning.platform.service.QuizService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizMapper quizMapper;

    @Override
    @Transactional
    public QuizResponse createQuiz(CreateQuizRequest request) {
        log.info("Creating quiz: {}", request.getTitle());

        // Проверяем модуль
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", request.getModuleId()));

        // Проверяем, нет ли уже теста у этого модуля
        if (module.getQuiz() != null) {
            throw new BadRequestException("Module already has a quiz");
        }

        // Создаем тест
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setTimeLimit(request.getTimeLimit());
        quiz.setModule(module);

        Quiz savedQuiz = quizRepository.save(quiz);

        // Создаем вопросы и варианты ответов
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            List<Question> questions = new ArrayList<>();

            for (CreateQuestionRequest questionRequest : request.getQuestions()) {
                Question question = new Question();
                question.setText(questionRequest.getText());
                question.setType(questionRequest.getType());
                question.setQuiz(savedQuiz);

                Question savedQuestion = questionRepository.save(question);

                // Создаем варианты ответов
                if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
                    List<AnswerOption> options = new ArrayList<>();

                    for (AnswerOptionRequest optionRequest : questionRequest.getOptions()) {
                        AnswerOption option = new AnswerOption();
                        option.setText(optionRequest.getText());
                        option.setIsCorrect(optionRequest.getIsCorrect());
                        option.setQuestion(savedQuestion);

                        options.add(answerOptionRepository.save(option));
                    }

                    savedQuestion.setOptions(options);
                }

                questions.add(savedQuestion);
            }

            savedQuiz.setQuestions(questions);
        }

        log.info("Quiz created with ID: {}", savedQuiz.getId());
        return quizMapper.toResponse(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        log.info("Getting quiz by ID: {}", id);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        return quizMapper.toResponse(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDetailResponse getQuizDetailById(Long id) {
        log.info("Getting quiz detail by ID: {}", id);

        Quiz quiz = quizRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        return toDetailResponse(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getAllQuizzes() {
        log.info("Getting all quizzes");

        List<Quiz> quizzes = quizRepository.findAll();

        return quizzes.stream()
                .map(quizMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long id, UpdateQuizRequest request) {
        log.info("Updating quiz with ID: {}", id);

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        if (request.getTitle() != null) {
            quiz.setTitle(request.getTitle());
        }
        if (request.getTimeLimit() != null) {
            quiz.setTimeLimit(request.getTimeLimit());
        }

        Quiz updatedQuiz = quizRepository.save(quiz);
        log.info("Quiz updated with ID: {}", updatedQuiz.getId());

        return quizMapper.toResponse(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        log.info("Deleting quiz with ID: {}", id);

        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", "id", id);
        }

        quizRepository.deleteById(id);
        log.info("Quiz deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public QuizResultResponse takeQuiz(Long studentId, TakeQuizRequest request) {
        log.info("Student {} taking quiz {}", studentId, request.getQuizId());

        // Проверяем студента
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        // Проверяем тест
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", request.getQuizId()));

        // Проверяем, не проходил ли уже студент этот тест
        Optional<QuizSubmission> existingSubmission = quizSubmissionRepository
                .findByQuizIdAndStudentId(request.getQuizId(), studentId);

        if (existingSubmission.isPresent()) {
            throw new BadRequestException("Student has already taken this quiz");
        }

        // Рассчитываем результат
        int score = calculateScore(request.getQuizId(), request.getAnswers());
        int maxScore = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
        int percentage = maxScore > 0 ? (score * 100) / maxScore : 0;

        // Создаем результат теста
        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setStudent(student);
        quizSubmission.setQuiz(quiz);
        quizSubmission.setScore(percentage); // Сохраняем процент
        quizSubmission.setTakenAt(LocalDateTime.now());

        QuizSubmission savedSubmission = quizSubmissionRepository.save(quizSubmission);
        log.info("Quiz submission created with ID: {}", savedSubmission.getId());

        return toResultResponse(savedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResultResponse> getQuizResultsByQuiz(Long quizId) {
        log.info("Getting quiz results for quiz ID: {}", quizId);

        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz", "id", quizId);
        }

        List<QuizSubmission> submissions = quizSubmissionRepository.findByQuizId(quizId);

        return submissions.stream()
                .map(this::toResultResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResultResponse> getQuizResultsByStudent(Long studentId) {
        log.info("Getting quiz results for student ID: {}", studentId);

        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("User", "id", studentId);
        }

        List<QuizSubmission> submissions = quizSubmissionRepository.findByStudentId(studentId);

        return submissions.stream()
                .map(this::toResultResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResultResponse getQuizResultById(Long id) {
        log.info("Getting quiz result by ID: {}", id);

        QuizSubmission submission = quizSubmissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSubmission", "id", id));

        return toResultResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getQuizzesByCourse(Long courseId) {
        log.info("Getting quizzes for course ID: {}", courseId);

        List<Quiz> allQuizzes = quizRepository.findAll();

        return allQuizzes.stream()
                .filter(quiz -> quiz.getModule() != null &&
                        quiz.getModule().getCourse() != null &&
                        quiz.getModule().getCourse().getId().equals(courseId))
                .map(quizMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getQuizzesByModule(Long moduleId) {
        log.info("Getting quizzes for module ID: {}", moduleId);

        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module", "id", moduleId);
        }

        Optional<Quiz> quiz = quizRepository.findByModuleId(moduleId);

        return quiz.stream()
                .map(quizMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int calculateScore(Long quizId, Map<Long, Long> answers) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        int score = 0;

        if (quiz.getQuestions() != null) {
            for (Question question : quiz.getQuestions()) {
                Long selectedOptionId = answers.get(question.getId());

                if (selectedOptionId != null) {
                    // Находим выбранный вариант ответа
                    Optional<AnswerOption> selectedOption = question.getOptions().stream()
                            .filter(option -> option.getId().equals(selectedOptionId))
                            .findFirst();

                    // Если вариант найден и он правильный, добавляем балл
                    if (selectedOption.isPresent() && Boolean.TRUE.equals(selectedOption.get().getIsCorrect())) {
                        score++;
                    }
                }
            }
        }

        return score;
    }

    private QuizDetailResponse toDetailResponse(Quiz quiz) {
        QuizDetailResponse response = QuizDetailResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .timeLimit(quiz.getTimeLimit())
                .build();

        if (quiz.getModule() != null) {
            response.setModuleId(quiz.getModule().getId());
            response.setModuleTitle(quiz.getModule().getTitle());

            if (quiz.getModule().getCourse() != null) {
                response.setCourseId(quiz.getModule().getCourse().getId());
                response.setCourseTitle(quiz.getModule().getCourse().getTitle());
            }
        }

        // Вопросы с вариантами ответов (без указания правильных ответов)
        if (quiz.getQuestions() != null) {
            response.setQuestions(quiz.getQuestions().stream()
                    .map(question -> {
                        QuizDetailResponse.QuestionResponse questionResponse =
                                new QuizDetailResponse.QuestionResponse();
                        questionResponse.setId(question.getId());
                        questionResponse.setText(question.getText());
                        questionResponse.setType(question.getType());

                        // Варианты ответов (без флага isCorrect)
                        if (question.getOptions() != null) {
                            questionResponse.setOptions(question.getOptions().stream()
                                    .map(option -> {
                                        QuizDetailResponse.AnswerOptionResponse optionResponse =
                                                new QuizDetailResponse.AnswerOptionResponse();
                                        optionResponse.setId(option.getId());
                                        optionResponse.setText(option.getText());
                                        return optionResponse;
                                    })
                                    .collect(Collectors.toList()));
                        }

                        return questionResponse;
                    })
                    .collect(Collectors.toList()));
        }

        return response;
    }

    private QuizResultResponse toResultResponse(QuizSubmission submission) {
        QuizResultResponse response = QuizResultResponse.builder()
                .id(submission.getId())
                .id(submission.getId())
                .score(submission.getScore())
                .takenAt(submission.getTakenAt())
                .build();

        if (submission.getStudent() != null) {
            response.setStudentId(submission.getStudent().getId());
            response.setStudentName(submission.getStudent().getName());
        }

        if (submission.getQuiz() != null) {
            response.setQuizId(submission.getQuiz().getId());
            response.setQuizTitle(submission.getQuiz().getTitle());

            // Максимальный возможный балл
            int maxScore = submission.getQuiz().getQuestions() != null ?
                    submission.getQuiz().getQuestions().size() : 0;
            response.setMaxScore(maxScore);

            // Абсолютный балл
            int absoluteScore = (submission.getScore() * maxScore) / 100;
            response.setAbsoluteScore(absoluteScore);
        }

        return response;
    }
}

