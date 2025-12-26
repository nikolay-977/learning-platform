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
import ru.skillfactory.learning.platform.dto.request.CreateLessonRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateLessonRequest;
import ru.skillfactory.learning.platform.dto.response.LessonResponse;
import ru.skillfactory.learning.platform.dto.response.LessonDetailResponse;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.service.LessonService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class LessonControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    private ObjectMapper objectMapper;
    private LessonResponse lessonResponse;
    private LessonDetailResponse lessonDetailResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(lessonController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        lessonResponse = new LessonResponse();
        lessonResponse.setId(1L);
        lessonResponse.setTitle("Lesson 1");
        lessonResponse.setContent("Lesson content");
        lessonResponse.setVideoUrl("video.mp4");
        lessonResponse.setModuleId(1L);
        lessonResponse.setModuleTitle("Module 1");
        lessonResponse.setAssignmentCount(2);

        lessonDetailResponse = new LessonDetailResponse();
        lessonDetailResponse.setId(1L);
        lessonDetailResponse.setTitle("Lesson 1");
        lessonDetailResponse.setContent("Lesson content");
        lessonDetailResponse.setVideoUrl("video.mp4");
        lessonDetailResponse.setModuleId(1L);
        lessonDetailResponse.setModuleTitle("Module 1");
        lessonDetailResponse.setCourseId(1L);
        lessonDetailResponse.setCourseTitle("Course 1");
    }

    @Test
    void createLesson_Success() throws Exception {
        // Given
        CreateLessonRequest request = new CreateLessonRequest();
        request.setTitle("Lesson 1");
        request.setContent("Lesson content");
        request.setVideoUrl("video.mp4");
        request.setModuleId(1L);

        when(lessonService.createLesson(any(CreateLessonRequest.class)))
                .thenReturn(lessonResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title", is("Lesson 1")))
                .andExpect(jsonPath("$.data.videoUrl", is("video.mp4")));

        verify(lessonService, times(1)).createLesson(any(CreateLessonRequest.class));
    }

    @Test
    void getLessonById_Success() throws Exception {
        // Given
        when(lessonService.getLessonById(1L))
                .thenReturn(lessonResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/lessons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.assignmentCount", is(2)));

        verify(lessonService, times(1)).getLessonById(1L);
    }

    @Test
    void getLessonsByModule_Success() throws Exception {
        // Given
        List<LessonResponse> lessons = Arrays.asList(lessonResponse);
        when(lessonService.getLessonsByModule(1L))
                .thenReturn(lessons);

        // When & Then
        mockMvc.perform(get("/api/v1/lessons/module/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(lessonService, times(1)).getLessonsByModule(1L);
    }

    @Test
    void updateLesson_Success() throws Exception {
        // Given
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setTitle("Updated Lesson");
        request.setContent("Updated content");

        LessonResponse updatedResponse = new LessonResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Updated Lesson");

        when(lessonService.updateLesson(eq(1L), any(UpdateLessonRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/lessons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Updated Lesson")));

        verify(lessonService, times(1)).updateLesson(eq(1L), any(UpdateLessonRequest.class));
    }

    @Test
    void deleteLesson_Success() throws Exception {
        // Given
        doNothing().when(lessonService).deleteLesson(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/lessons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(lessonService, times(1)).deleteLesson(1L);
    }

    @Test
    void getLessonDetailById_Success() throws Exception {
        // Given
        when(lessonService.getLessonDetailById(1L))
                .thenReturn(lessonDetailResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/lessons/1/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.courseTitle", is("Course 1")));

        verify(lessonService, times(1)).getLessonDetailById(1L);
    }
}
