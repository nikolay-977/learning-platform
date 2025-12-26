package ru.skillfactory.learning.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.skillfactory.learning.platform.dto.request.SubmitAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateSubmissionRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.SubmissionDetailResponse;
import ru.skillfactory.learning.platform.dto.response.SubmissionResponse;
import ru.skillfactory.learning.platform.service.SubmissionService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubmissionControllerTest {

    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private SubmissionController submissionController;

    private SubmissionResponse submissionResponse;
    private SubmissionDetailResponse submissionDetailResponse;
    private SubmitAssignmentRequest submitRequest;
    private UpdateSubmissionRequest updateRequest;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        // Настройка тестовых данных
        submissionResponse = SubmissionResponse.builder()
                .id(1L)
                .content("Решение задания")
                .submittedAt(LocalDateTime.now())
                .score(85)
                .feedback("Хорошая работа")
                .assignmentId(100L)
                .assignmentTitle("Задание 1")
                .studentId(50L)
                .studentName("Иван Иванов")
                .build();

        submissionDetailResponse = new SubmissionDetailResponse();
        submissionDetailResponse.setId(1L);
        submissionDetailResponse.setContent("Подробное решение");
        submissionDetailResponse.setSubmittedAt(LocalDateTime.now());
        submissionDetailResponse.setScore(90);
        submissionDetailResponse.setFeedback("Отличная работа!");
        submissionDetailResponse.setStudentId(50L);
        submissionDetailResponse.setStudentName("Иван Иванов");
        submissionDetailResponse.setStudentEmail("ivan@example.com");
        submissionDetailResponse.setAssignmentId(100L);
        submissionDetailResponse.setAssignmentTitle("Задание 1");
        submissionDetailResponse.setMaxScore(100);
        submissionDetailResponse.setCourseId(10L);
        submissionDetailResponse.setCourseTitle("Курс программирования");

        submitRequest = new SubmitAssignmentRequest();
        submitRequest.setAssignmentId(100L);
        submitRequest.setContent("Мое решение");

        updateRequest = new UpdateSubmissionRequest();
        updateRequest.setContent("Обновленное решение");
        updateRequest.setScore(95);
        updateRequest.setFeedback("Отлично!");
    }

    @Test
    void submitAssignment_Success() {
        // Arrange
        Long studentId = 50L;
        when(submissionService.submitAssignment(eq(studentId), any(SubmitAssignmentRequest.class)))
                .thenReturn(submissionResponse);

        // Act
        ResponseEntity<ApiResponse<SubmissionResponse>> response =
                submissionController.submitAssignment(studentId, submitRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Assignment submitted successfully", response.getBody().getMessage());
        assertEquals(submissionResponse, response.getBody().getData());

        verify(submissionService, times(1))
                .submitAssignment(eq(studentId), any(SubmitAssignmentRequest.class));
    }

    @Test
    void getSubmissionById_Success() {
        // Arrange
        Long submissionId = 1L;
        when(submissionService.getSubmissionById(submissionId))
                .thenReturn(submissionResponse);

        // Act
        ResponseEntity<ApiResponse<SubmissionResponse>> response =
                submissionController.getSubmissionById(submissionId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(submissionResponse, response.getBody().getData());

        verify(submissionService, times(1)).getSubmissionById(submissionId);
    }

    @Test
    void getSubmissionDetailById_Success() {
        // Arrange
        Long submissionId = 1L;
        when(submissionService.getSubmissionDetailById(submissionId))
                .thenReturn(submissionDetailResponse);

        // Act
        ResponseEntity<ApiResponse<SubmissionDetailResponse>> response =
                submissionController.getSubmissionDetailById(submissionId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(submissionDetailResponse, response.getBody().getData());

        verify(submissionService, times(1)).getSubmissionDetailById(submissionId);
    }

    @Test
    void getSubmissionsByAssignment_Success() {
        // Arrange
        Long assignmentId = 100L;
        List<SubmissionResponse> submissions = Arrays.asList(
                submissionResponse,
                SubmissionResponse.builder()
                        .id(2L)
                        .content("Другое решение")
                        .score(75)
                        .assignmentId(assignmentId)
                        .studentId(51L)
                        .studentName("Петр Петров")
                        .build()
        );
        when(submissionService.getSubmissionsByAssignment(assignmentId))
                .thenReturn(submissions);

        // Act
        ResponseEntity<ApiResponse<List<SubmissionResponse>>> response =
                submissionController.getSubmissionsByAssignment(assignmentId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(submissions, response.getBody().getData());

        verify(submissionService, times(1)).getSubmissionsByAssignment(assignmentId);
    }

    @Test
    void getSubmissionsByStudent_Success() {
        // Arrange
        Long studentId = 50L;
        List<SubmissionResponse> submissions = Arrays.asList(
                submissionResponse,
                SubmissionResponse.builder()
                        .id(2L)
                        .content("Решение задания 2")
                        .score(90)
                        .assignmentId(101L)
                        .studentId(studentId)
                        .studentName("Иван Иванов")
                        .build()
        );
        when(submissionService.getSubmissionsByStudent(studentId))
                .thenReturn(submissions);

        // Act
        ResponseEntity<ApiResponse<List<SubmissionResponse>>> response =
                submissionController.getSubmissionsByStudent(studentId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(submissions, response.getBody().getData());

        verify(submissionService, times(1)).getSubmissionsByStudent(studentId);
    }

    @Test
    void getAllSubmissions_Success() {
        // Arrange
        List<SubmissionResponse> submissions = Arrays.asList(
                submissionResponse,
                SubmissionResponse.builder()
                        .id(2L)
                        .content("Другое решение")
                        .score(70)
                        .assignmentId(101L)
                        .studentId(51L)
                        .studentName("Петр Петров")
                        .build()
        );
        when(submissionService.getAllSubmissions())
                .thenReturn(submissions);

        // Act
        ResponseEntity<ApiResponse<List<SubmissionResponse>>> response =
                submissionController.getAllSubmissions();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(submissions, response.getBody().getData());

        verify(submissionService, times(1)).getAllSubmissions();
    }

    @Test
    void getUngradedSubmissions_Success() {
        // Arrange
        SubmissionResponse ungradedSubmission = SubmissionResponse.builder()
                .id(3L)
                .content("Непроверенное решение")
                .score(null) // Не проверено
                .assignmentId(100L)
                .studentId(52L)
                .studentName("Сергей Сергеев")
                .build();

        List<SubmissionResponse> ungradedSubmissions = Arrays.asList(ungradedSubmission);
        when(submissionService.getUngradedSubmissions())
                .thenReturn(ungradedSubmissions);

        // Act
        ResponseEntity<ApiResponse<List<SubmissionResponse>>> response =
                submissionController.getUngradedSubmissions();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(ungradedSubmissions, response.getBody().getData());

        verify(submissionService, times(1)).getUngradedSubmissions();
    }

    @Test
    void getSubmissionsByCourse_Success() {
        // Arrange
        Long courseId = 10L;
        List<SubmissionResponse> submissions = Arrays.asList(
                submissionResponse,
                SubmissionResponse.builder()
                        .id(2L)
                        .content("Решение из того же курса")
                        .score(88)
                        .assignmentId(101L)
                        .studentId(53L)
                        .studentName("Анна Аннова")
                        .build()
        );
        when(submissionService.getSubmissionsByCourse(courseId))
                .thenReturn(submissions);

        // Act
        ResponseEntity<ApiResponse<List<SubmissionResponse>>> response =
                submissionController.getSubmissionsByCourse(courseId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(submissions, response.getBody().getData());

        verify(submissionService, times(1)).getSubmissionsByCourse(courseId);
    }

    @Test
    void hasStudentSubmitted_Success() {
        // Arrange
        Long studentId = 50L;
        Long assignmentId = 100L;
        when(submissionService.hasStudentSubmitted(studentId, assignmentId))
                .thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                submissionController.hasStudentSubmitted(studentId, assignmentId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData());

        verify(submissionService, times(1)).hasStudentSubmitted(studentId, assignmentId);
    }

    @Test
    void hasStudentSubmitted_False() {
        // Arrange
        Long studentId = 50L;
        Long assignmentId = 100L;
        when(submissionService.hasStudentSubmitted(studentId, assignmentId))
                .thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<Boolean>> response =
                submissionController.hasStudentSubmitted(studentId, assignmentId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertFalse(response.getBody().getData());

        verify(submissionService, times(1)).hasStudentSubmitted(studentId, assignmentId);
    }

    @Test
    void updateSubmission_Success() {
        // Arrange
        Long submissionId = 1L;
        SubmissionResponse updatedResponse = SubmissionResponse.builder()
                .id(submissionId)
                .content("Обновленное решение")
                .score(95)
                .feedback("Отлично!")
                .assignmentId(100L)
                .studentId(50L)
                .studentName("Иван Иванов")
                .build();

        when(submissionService.updateSubmission(eq(submissionId), any(UpdateSubmissionRequest.class)))
                .thenReturn(updatedResponse);

        // Act
        ResponseEntity<ApiResponse<SubmissionResponse>> response =
                submissionController.updateSubmission(submissionId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Submission updated successfully", response.getBody().getMessage());
        assertEquals(updatedResponse, response.getBody().getData());

        verify(submissionService, times(1))
                .updateSubmission(eq(submissionId), any(UpdateSubmissionRequest.class));
    }

    @Test
    void gradeSubmission_Success() {
        // Arrange
        Long submissionId = 1L;
        Integer score = 90;
        String feedback = "Хорошая работа";

        SubmissionResponse gradedResponse = SubmissionResponse.builder()
                .id(submissionId)
                .content("Решение")
                .score(score)
                .feedback(feedback)
                .assignmentId(100L)
                .studentId(50L)
                .studentName("Иван Иванов")
                .build();

        when(submissionService.gradeSubmission(submissionId, score, feedback))
                .thenReturn(gradedResponse);

        // Act
        ResponseEntity<ApiResponse<SubmissionResponse>> response =
                submissionController.gradeSubmission(submissionId, score, feedback);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Submission graded", response.getBody().getMessage());
        assertEquals(gradedResponse, response.getBody().getData());

        verify(submissionService, times(1)).gradeSubmission(submissionId, score, feedback);
    }

    @Test
    void gradeSubmission_Success_WithoutFeedback() {
        // Arrange
        Long submissionId = 1L;
        Integer score = 90;
        String feedback = null;

        SubmissionResponse gradedResponse = SubmissionResponse.builder()
                .id(submissionId)
                .content("Решение")
                .score(score)
                .feedback(null)
                .assignmentId(100L)
                .studentId(50L)
                .studentName("Иван Иванов")
                .build();

        when(submissionService.gradeSubmission(submissionId, score, feedback))
                .thenReturn(gradedResponse);

        // Act
        ResponseEntity<ApiResponse<SubmissionResponse>> response =
                submissionController.gradeSubmission(submissionId, score, feedback);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Submission graded", response.getBody().getMessage());
        assertEquals(gradedResponse, response.getBody().getData());

        verify(submissionService, times(1)).gradeSubmission(submissionId, score, feedback);
    }

    @Test
    void deleteSubmission_Success() {
        // Arrange
        Long submissionId = 1L;
        doNothing().when(submissionService).deleteSubmission(submissionId);

        // Act
        ResponseEntity<ApiResponse<Void>> response =
                submissionController.deleteSubmission(submissionId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Submission deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());

        verify(submissionService, times(1)).deleteSubmission(submissionId);
    }

    @Test
    void testApiResponseStructure() throws Exception {
        // Arrange
        Long submissionId = 1L;
        when(submissionService.getSubmissionById(submissionId))
                .thenReturn(submissionResponse);

        // Act
        ResponseEntity<ApiResponse<SubmissionResponse>> response =
                submissionController.getSubmissionById(submissionId);

        // Assert
        String jsonResponse = objectMapper.writeValueAsString(response.getBody());
        assertTrue(jsonResponse.contains("\"success\":true"));
        assertTrue(jsonResponse.contains("\"message\":\"Operation successful\""));
        assertTrue(jsonResponse.contains("\"data\":"));

        verify(submissionService, times(1)).getSubmissionById(submissionId);
    }

    @Test
    void testEmptyListResponses() {
        // Arrange
        Long assignmentId = 999L;
        when(submissionService.getSubmissionsByAssignment(assignmentId))
                .thenReturn(List.of());

        // Act
        ResponseEntity<ApiResponse<List<SubmissionResponse>>> response =
                submissionController.getSubmissionsByAssignment(assignmentId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().isEmpty());

        verify(submissionService, times(1)).getSubmissionsByAssignment(assignmentId);
    }
}
