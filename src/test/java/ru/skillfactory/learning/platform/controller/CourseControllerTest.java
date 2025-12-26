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
import ru.skillfactory.learning.platform.dto.request.CreateCourseRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCourseRequest;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;
import ru.skillfactory.learning.platform.dto.response.CourseDetailResponse;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.service.CourseService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private ObjectMapper objectMapper;

    private CourseResponse courseResponse1;
    private CourseResponse courseResponse2;
    private CourseDetailResponse courseDetailResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Настройка тестовых данных
        courseResponse1 = new CourseResponse();
        courseResponse1.setId(1L);
        courseResponse1.setTitle("Java Programming");
        courseResponse1.setDescription("Learn Java from scratch");
        courseResponse1.setTeacherId(1L);
        courseResponse1.setTeacherName("John Teacher");
        courseResponse1.setCategoryId(1L);
        courseResponse1.setCategoryName("Programming");
        courseResponse1.setStartDate(LocalDate.now().plusDays(1));

        courseResponse2 = new CourseResponse();
        courseResponse2.setId(2L);
        courseResponse2.setTitle("Spring Boot");
        courseResponse2.setDescription("Learn Spring Boot framework");
        courseResponse2.setTeacherId(1L);
        courseResponse2.setTeacherName("John Teacher");
        courseResponse2.setCategoryId(1L);
        courseResponse2.setCategoryName("Programming");
        courseResponse2.setStartDate(LocalDate.now().plusDays(1));

        courseDetailResponse = new CourseDetailResponse();
        courseDetailResponse.setId(1L);
        courseDetailResponse.setTitle("Java Programming");
        courseDetailResponse.setDescription("Learn Java from scratch");
        courseDetailResponse.setTeacherId(1L);
        courseDetailResponse.setTeacherName("John Teacher");
        courseDetailResponse.setCategoryId(1L);
        courseDetailResponse.setCategoryName("Programming");
        courseDetailResponse.setAverageRating(4.5);
    }

    @Test
    void createCourse_Success() throws Exception {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Java Programming");
        request.setDescription("Learn Java from scratch");
        request.setTeacherId(1L);
        request.setCategoryId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));

        when(courseService.createCourse(any(CreateCourseRequest.class)))
                .thenReturn(courseResponse1);

        // When & Then
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Course created successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Java Programming")));

        verify(courseService, times(1)).createCourse(any(CreateCourseRequest.class));
    }

    @Test
    void createCourse_ValidationFailed() throws Exception {
        // Given - Пустой запрос (без обязательных полей)
        CreateCourseRequest request = new CreateCourseRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")))
                .andExpect(jsonPath("$.validationErrors").exists())
                .andExpect(jsonPath("$.validationErrors.title", is("Title is required")))
                .andExpect(jsonPath("$.validationErrors.categoryId", is("Category ID is required")))
                .andExpect(jsonPath("$.validationErrors.teacherId", is("Teacher ID is required")));

        verify(courseService, never()).createCourse(any());
    }

    @Test
    void getCourseById_Success() throws Exception {
        // Given
        when(courseService.getCourseById(1L))
                .thenReturn(courseResponse1);

        // When & Then
        mockMvc.perform(get("/api/v1/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Java Programming")));

        verify(courseService, times(1)).getCourseById(1L);
    }

    @Test
    void getCourseDetailById_Success() throws Exception {
        // Given
        when(courseService.getCourseDetailById(1L))
                .thenReturn(courseDetailResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/courses/1/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Java Programming")))
                .andExpect(jsonPath("$.data.averageRating", is(4.5)));

        verify(courseService, times(1)).getCourseDetailById(1L);
    }

    @Test
    void getAllCourses_Success() throws Exception {
        // Given
        List<CourseResponse> courses = Arrays.asList(courseResponse1, courseResponse2);
        when(courseService.getAllCourses())
                .thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].title", is("Java Programming")))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].title", is("Spring Boot")));

        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void getCoursesByCategory_Success() throws Exception {
        // Given
        List<CourseResponse> courses = Arrays.asList(courseResponse1, courseResponse2);
        when(courseService.getCoursesByCategory(1L))
                .thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/v1/courses/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(courseService, times(1)).getCoursesByCategory(1L);
    }

    @Test
    void getCoursesByTeacher_Success() throws Exception {
        // Given
        List<CourseResponse> courses = Arrays.asList(courseResponse1, courseResponse2);
        when(courseService.getCoursesByTeacher(1L))
                .thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/v1/courses/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(courseService, times(1)).getCoursesByTeacher(1L);
    }

    @Test
    void searchCourses_Success() throws Exception {
        // Given
        List<CourseResponse> courses = Arrays.asList(courseResponse1);
        when(courseService.searchCourses("Java"))
                .thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/v1/courses/search")
                        .param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", containsString("Java")));

        verify(courseService, times(1)).searchCourses("Java");
    }

    @Test
    void updateCourse_Success() throws Exception {
        // Given
        UpdateCourseRequest request = new UpdateCourseRequest();
        request.setTitle("Updated Java Course");
        request.setDescription("Updated description");

        CourseResponse updatedResponse = new CourseResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Updated Java Course");

        when(courseService.updateCourse(eq(1L), any(UpdateCourseRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Course updated successfully")))
                .andExpect(jsonPath("$.data.title", is("Updated Java Course")));

        verify(courseService, times(1)).updateCourse(eq(1L), any(UpdateCourseRequest.class));
    }

    @Test
    void deleteCourse_Success() throws Exception {
        // Given
        doNothing().when(courseService).deleteCourse(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Course deleted successfully")));

        verify(courseService, times(1)).deleteCourse(1L);
    }

    @Test
    void addTagToCourse_Success() throws Exception {
        // Given
        when(courseService.addTagToCourse(1L, 1L))
                .thenReturn(courseResponse1);

        // When & Then
        mockMvc.perform(put("/api/v1/courses/1/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Tag added to course")));

        verify(courseService, times(1)).addTagToCourse(1L, 1L);
    }

    @Test
    void getEnrolledCourses_Success() throws Exception {
        // Given
        List<CourseResponse> courses = Arrays.asList(courseResponse1);
        when(courseService.getEnrolledCourses(1L))
                .thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/api/v1/courses/enrolled/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(courseService, times(1)).getEnrolledCourses(1L);
    }

    @Test
    void isUserEnrolled_Success() throws Exception {
        // Given
        when(courseService.isUserEnrolled(1L, 1L))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/courses/1/enrolled/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(true)));

        verify(courseService, times(1)).isUserEnrolled(1L, 1L);
    }
}
