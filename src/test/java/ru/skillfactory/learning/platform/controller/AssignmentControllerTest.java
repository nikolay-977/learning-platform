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
import ru.skillfactory.learning.platform.dto.request.CreateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.response.AssignmentResponse;
import ru.skillfactory.learning.platform.dto.response.AssignmentDetailResponse;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.service.AssignmentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AssignmentService assignmentService;

    @InjectMocks
    private AssignmentController assignmentController;

    private ObjectMapper objectMapper;
    private AssignmentResponse assignmentResponse;
    private AssignmentDetailResponse assignmentDetailResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(assignmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        assignmentResponse = new AssignmentResponse();
        assignmentResponse.setId(1L);
        assignmentResponse.setTitle("Homework 1");
        assignmentResponse.setDescription("Complete exercises 1-5");
        assignmentResponse.setDueDate(LocalDate.now().plusDays(7));
        assignmentResponse.setMaxScore(100);
        assignmentResponse.setLessonId(1L);
        assignmentResponse.setLessonTitle("Lesson 1");

        assignmentDetailResponse = new AssignmentDetailResponse();
        assignmentDetailResponse.setId(1L);
        assignmentDetailResponse.setTitle("Homework 1");
        assignmentDetailResponse.setDescription("Complete exercises 1-5");
        assignmentDetailResponse.setDueDate(LocalDate.now().plusDays(7));
        assignmentDetailResponse.setMaxScore(100);
        assignmentDetailResponse.setLessonId(1L);
        assignmentDetailResponse.setLessonTitle("Lesson 1");
        assignmentDetailResponse.setSubmissionsCount(5);
    }

    @Test
    void createAssignment_Success() throws Exception {
        // Given
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setTitle("Homework 1");
        request.setDescription("Complete exercises 1-5");
        request.setLessonId(1L);
        request.setDueDate(LocalDate.now().plusDays(7));
        request.setMaxScore(100);

        when(assignmentService.createAssignment(any(CreateAssignmentRequest.class)))
                .thenReturn(assignmentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Homework 1")))
                .andExpect(jsonPath("$.data.maxScore", is(100)));

        verify(assignmentService, times(1)).createAssignment(any(CreateAssignmentRequest.class));
    }

    @Test
    void getAssignmentById_Success() throws Exception {
        // Given
        when(assignmentService.getAssignmentById(1L))
                .thenReturn(assignmentResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/assignments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)));

        verify(assignmentService, times(1)).getAssignmentById(1L);
    }

    @Test
    void getAssignmentDetailById_Success() throws Exception {
        // Given
        when(assignmentService.getAssignmentDetailById(1L))
                .thenReturn(assignmentDetailResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/assignments/1/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.submissionsCount", is(5)));

        verify(assignmentService, times(1)).getAssignmentDetailById(1L);
    }

    @Test
    void getAssignmentsByLesson_Success() throws Exception {
        // Given
        List<AssignmentResponse> assignments = Arrays.asList(assignmentResponse);
        when(assignmentService.getAssignmentsByLesson(1L))
                .thenReturn(assignments);

        // When & Then
        mockMvc.perform(get("/api/v1/assignments/lesson/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(assignmentService, times(1)).getAssignmentsByLesson(1L);
    }

    @Test
    void getAllAssignments_Success() throws Exception {
        // Given
        List<AssignmentResponse> assignments = Arrays.asList(assignmentResponse);
        when(assignmentService.getAllAssignments())
                .thenReturn(assignments);

        // When & Then
        mockMvc.perform(get("/api/v1/assignments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(assignmentService, times(1)).getAllAssignments();
    }

    @Test
    void updateAssignment_Success() throws Exception {
        // Given
        UpdateAssignmentRequest request = new UpdateAssignmentRequest();
        request.setTitle("Updated Homework");
        request.setMaxScore(90);

        AssignmentResponse updatedResponse = new AssignmentResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Updated Homework");
        updatedResponse.setMaxScore(90);

        when(assignmentService.updateAssignment(eq(1L), any(UpdateAssignmentRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Updated Homework")));

        verify(assignmentService, times(1)).updateAssignment(eq(1L), any(UpdateAssignmentRequest.class));
    }

    @Test
    void deleteAssignment_Success() throws Exception {
        // Given
        doNothing().when(assignmentService).deleteAssignment(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/assignments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(assignmentService, times(1)).deleteAssignment(1L);
    }

    @Test
    void getAssignmentsDueSoon_Success() throws Exception {
        // Given
        List<AssignmentResponse> assignments = Arrays.asList(assignmentResponse);
        when(assignmentService.getAssignmentsDueSoon())
                .thenReturn(assignments);

        // When & Then
        mockMvc.perform(get("/api/v1/assignments/due-soon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(assignmentService, times(1)).getAssignmentsDueSoon();
    }
}
