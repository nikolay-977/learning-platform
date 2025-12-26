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
import ru.skillfactory.learning.platform.dto.request.EnrollRequest;
import ru.skillfactory.learning.platform.dto.response.EnrollmentResponse;
import ru.skillfactory.learning.platform.entity.EnrollmentStatus;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.service.EnrollmentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private ObjectMapper objectMapper;

    private EnrollmentResponse enrollmentResponse1;
    private EnrollmentResponse enrollmentResponse2;

    @BeforeEach
    void setUp() {
        // Настройка ObjectMapper с поддержкой Java 8 Date/Time
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Настройка тестовых данных
        enrollmentResponse1 = new EnrollmentResponse();
        enrollmentResponse1.setId(1L);
        enrollmentResponse1.setStudentId(1L);
        enrollmentResponse1.setStudentName("John Student");
        enrollmentResponse1.setCourseId(1L);
        enrollmentResponse1.setCourseTitle("Java Programming");
        enrollmentResponse1.setEnrollDate(LocalDate.now());
        enrollmentResponse1.setStatus(EnrollmentStatus.ACTIVE);

        enrollmentResponse2 = new EnrollmentResponse();
        enrollmentResponse2.setId(2L);
        enrollmentResponse2.setStudentId(2L);
        enrollmentResponse2.setStudentName("Jane Student");
        enrollmentResponse2.setCourseId(1L);
        enrollmentResponse2.setCourseTitle("Java Programming");
        enrollmentResponse2.setEnrollDate(LocalDate.now());
        enrollmentResponse2.setStatus(EnrollmentStatus.ACTIVE);
    }

    @Test
    void enrollStudent_Success() throws Exception {
        // Given
        EnrollRequest request = new EnrollRequest();
        request.setStudentId(1L);
        request.setCourseId(1L);

        when(enrollmentService.enrollStudent(any(EnrollRequest.class)))
                .thenReturn(enrollmentResponse1);

        // When & Then
        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Student enrolled successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.studentId", is(1)))
                .andExpect(jsonPath("$.data.courseId", is(1)))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));

        verify(enrollmentService, times(1)).enrollStudent(any(EnrollRequest.class));
    }

    @Test
    void enrollStudent_ValidationFailed() throws Exception {
        // Given - Пустой запрос
        EnrollRequest request = new EnrollRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")));

        verify(enrollmentService, never()).enrollStudent(any());
    }

    @Test
    void getEnrollmentById_Success() throws Exception {
        // Given
        when(enrollmentService.getEnrollmentById(1L))
                .thenReturn(enrollmentResponse1);

        // When & Then
        mockMvc.perform(get("/api/v1/enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.studentName", is("John Student")))
                .andExpect(jsonPath("$.data.courseTitle", is("Java Programming")));

        verify(enrollmentService, times(1)).getEnrollmentById(1L);
    }

    @Test
    void getEnrollmentsByStudent_Success() throws Exception {
        // Given
        List<EnrollmentResponse> enrollments = Arrays.asList(enrollmentResponse1);
        when(enrollmentService.getEnrollmentsByStudent(1L))
                .thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/api/v1/enrollments/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].studentId", is(1)))
                .andExpect(jsonPath("$.data[0].studentName", is("John Student")));

        verify(enrollmentService, times(1)).getEnrollmentsByStudent(1L);
    }

    @Test
    void getEnrollmentsByCourse_Success() throws Exception {
        // Given
        List<EnrollmentResponse> enrollments = Arrays.asList(enrollmentResponse1, enrollmentResponse2);
        when(enrollmentService.getEnrollmentsByCourse(1L))
                .thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/api/v1/enrollments/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].courseId", is(1)))
                .andExpect(jsonPath("$.data[1].courseId", is(1)));

        verify(enrollmentService, times(1)).getEnrollmentsByCourse(1L);
    }

    @Test
    void getAllEnrollments_Success() throws Exception {
        // Given
        List<EnrollmentResponse> enrollments = Arrays.asList(enrollmentResponse1, enrollmentResponse2);
        when(enrollmentService.getAllEnrollments())
                .thenReturn(enrollments);

        // When & Then
        mockMvc.perform(get("/api/v1/enrollments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(enrollmentService, times(1)).getAllEnrollments();
    }

    @Test
    void isStudentEnrolled_Success() throws Exception {
        // Given
        when(enrollmentService.isStudentEnrolled(1L, 1L))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/enrollments/student/1/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(true)));

        verify(enrollmentService, times(1)).isStudentEnrolled(1L, 1L);
    }

    @Test
    void getActiveEnrollmentsCount_Success() throws Exception {
        // Given
        when(enrollmentService.getActiveEnrollmentsCount(1L))
                .thenReturn(5);

        // When & Then
        mockMvc.perform(get("/api/v1/enrollments/course/1/active/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(5)));

        verify(enrollmentService, times(1)).getActiveEnrollmentsCount(1L);
    }

    @Test
    void updateEnrollmentStatus_Success() throws Exception {
        // Given
        EnrollmentResponse updatedResponse = new EnrollmentResponse();
        updatedResponse.setId(1L);
        updatedResponse.setStatus(EnrollmentStatus.COMPLETED);

        when(enrollmentService.updateEnrollmentStatus(1L, "COMPLETED"))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/enrollments/1/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Enrollment status updated")))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));

        verify(enrollmentService, times(1)).updateEnrollmentStatus(1L, "COMPLETED");
    }

    @Test
    void completeCourse_Success() throws Exception {
        // Given
        EnrollmentResponse completedResponse = new EnrollmentResponse();
        completedResponse.setId(1L);
        completedResponse.setStatus(EnrollmentStatus.COMPLETED);

        when(enrollmentService.completeCourse(1L))
                .thenReturn(completedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/enrollments/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Course marked as completed")))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));

        verify(enrollmentService, times(1)).completeCourse(1L);
    }

    @Test
    void cancelEnrollment_Success() throws Exception {
        // Given
        doNothing().when(enrollmentService).cancelEnrollment(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/enrollments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Enrollment canceled")));

        verify(enrollmentService, times(1)).cancelEnrollment(1L);
    }

    @Test
    void cancelEnrollmentByStudentAndCourse_Success() throws Exception {
        // Given
        doNothing().when(enrollmentService).cancelEnrollmentByStudentAndCourse(1L, 1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/enrollments/student/1/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Enrollment canceled")));

        verify(enrollmentService, times(1)).cancelEnrollmentByStudentAndCourse(1L, 1L);
    }

    @Test
    void getEnrollmentById_NotFound() throws Exception {
        // Given
        when(enrollmentService.getEnrollmentById(999L))
                .thenThrow(new ru.skillfactory.learning.platform.exception.ResourceNotFoundException("Enrollment", "id", 999L));

        // When & Then
        mockMvc.perform(get("/api/v1/enrollments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode", is("RESOURCE_NOT_FOUND")));

        verify(enrollmentService, times(1)).getEnrollmentById(999L);
    }

    @Test
    void updateEnrollmentStatus_InvalidStatus() throws Exception {
        // Given
        when(enrollmentService.updateEnrollmentStatus(1L, "INVALID_STATUS"))
                .thenThrow(new ru.skillfactory.learning.platform.exception.BadRequestException("Invalid enrollment status: INVALID_STATUS"));

        // When & Then
        mockMvc.perform(put("/api/v1/enrollments/1/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("BAD_REQUEST")));

        verify(enrollmentService, times(1)).updateEnrollmentStatus(1L, "INVALID_STATUS");
    }
}