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
import ru.skillfactory.learning.platform.dto.request.CreateModuleRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateModuleRequest;
import ru.skillfactory.learning.platform.dto.response.ModuleResponse;
import ru.skillfactory.learning.platform.dto.response.ModuleDetailResponse;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.service.ModuleService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ModuleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ModuleService moduleService;

    @InjectMocks
    private ModuleController moduleController;

    private ObjectMapper objectMapper;
    private ModuleResponse moduleResponse;
    private ModuleDetailResponse moduleDetailResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(moduleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        moduleResponse = new ModuleResponse();
        moduleResponse.setId(1L);
        moduleResponse.setTitle("Module 1");
        moduleResponse.setDescription("First module");
        moduleResponse.setOrderIndex(1);
        moduleResponse.setCourseId(1L);
        moduleResponse.setCourseTitle("Course 1");
        moduleResponse.setLessonCount(3);
        moduleResponse.setHasQuiz(true);

        moduleDetailResponse = new ModuleDetailResponse();
        moduleDetailResponse.setId(1L);
        moduleDetailResponse.setTitle("Module 1");
        moduleDetailResponse.setDescription("First module");
        moduleDetailResponse.setOrderIndex(1);
        moduleDetailResponse.setCourseId(1L);
        moduleDetailResponse.setCourseTitle("Course 1");
        moduleDetailResponse.setHasQuiz(true);
        moduleDetailResponse.setQuizId(1L);
        moduleDetailResponse.setQuizTitle("Quiz 1");
    }

    @Test
    void createModule_Success() throws Exception {
        // Given
        CreateModuleRequest request = new CreateModuleRequest();
        request.setTitle("Module 1");
        request.setDescription("First module");
        request.setCourseId(1L);
        request.setOrderIndex(1);

        when(moduleService.createModule(any(CreateModuleRequest.class)))
                .thenReturn(moduleResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.orderIndex", is(1)));

        verify(moduleService, times(1)).createModule(any(CreateModuleRequest.class));
    }

    @Test
    void getModuleById_Success() throws Exception {
        // Given
        when(moduleService.getModuleById(1L))
                .thenReturn(moduleResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasQuiz", is(true)));

        verify(moduleService, times(1)).getModuleById(1L);
    }

    @Test
    void getModulesByCourse_Success() throws Exception {
        // Given
        List<ModuleResponse> modules = Arrays.asList(moduleResponse);
        when(moduleService.getModulesByCourse(1L))
                .thenReturn(modules);

        // When & Then
        mockMvc.perform(get("/api/v1/modules/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(moduleService, times(1)).getModulesByCourse(1L);
    }

    @Test
    void updateModule_Success() throws Exception {
        // Given
        UpdateModuleRequest request = new UpdateModuleRequest();
        request.setTitle("Updated Module");

        ModuleResponse updatedResponse = new ModuleResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Updated Module");

        when(moduleService.updateModule(eq(1L), any(UpdateModuleRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/modules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Updated Module")));

        verify(moduleService, times(1)).updateModule(eq(1L), any(UpdateModuleRequest.class));
    }

    @Test
    void deleteModule_Success() throws Exception {
        // Given
        doNothing().when(moduleService).deleteModule(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/modules/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(moduleService, times(1)).deleteModule(1L);
    }

    @Test
    void getModuleDetailById_Success() throws Exception {
        // Given
        when(moduleService.getModuleDetailById(1L))
                .thenReturn(moduleDetailResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/modules/1/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quizTitle", is("Quiz 1")));

        verify(moduleService, times(1)).getModuleDetailById(1L);
    }

    @Test
    void reorderModule_Success() throws Exception {
        // Given
        when(moduleService.reorderModule(1L, 2))
                .thenReturn(moduleResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/modules/1/reorder/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(moduleService, times(1)).reorderModule(1L, 2);
    }
}