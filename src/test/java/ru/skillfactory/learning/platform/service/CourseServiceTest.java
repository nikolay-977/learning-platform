package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.CreateCourseRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCourseRequest;
import ru.skillfactory.learning.platform.dto.response.CourseResponse;
import ru.skillfactory.learning.platform.entity.Category;
import ru.skillfactory.learning.platform.entity.Course;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.CourseMapper;
import ru.skillfactory.learning.platform.repository.*;
import ru.skillfactory.learning.platform.service.impl.CourseServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private CourseMapper courseMapper;

    @InjectMocks
    private CourseServiceImpl courseService;

    private User testTeacher;
    private Category testCategory;
    private Course testCourse;
    private CourseResponse testCourseResponse;

    @BeforeEach
    void setUp() {
        testTeacher = new User();
        testTeacher.setId(1L);
        testTeacher.setName("John Teacher");
        testTeacher.setEmail("teacher@example.com");
        testTeacher.setRole(Role.TEACHER);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Programming");

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Java Basics");
        testCourse.setDescription("Learn Java programming");
        testCourse.setTeacher(testTeacher);
        testCourse.setCategory(testCategory);
        testCourse.setStartDate(LocalDate.now().plusDays(1));

        testCourseResponse = new CourseResponse();
        testCourseResponse.setId(1L);
        testCourseResponse.setTitle("Java Basics");
        testCourseResponse.setTeacherId(1L);
        testCourseResponse.setTeacherName("John Teacher");
        testCourseResponse.setCategoryId(1L);
        testCourseResponse.setCategoryName("Programming");
    }

    @Test
    void createCourse_Success() {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Java Basics");
        request.setDescription("Learn Java programming");
        request.setTeacherId(1L);
        request.setCategoryId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);
        when(courseMapper.toResponse(any(Course.class))).thenReturn(testCourseResponse);

        // When
        CourseResponse response = courseService.createCourse(request);

        // Then
        assertNotNull(response);
        assertEquals("Java Basics", response.getTitle());
        assertEquals(1L, response.getTeacherId());

        verify(userRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_TeacherNotFound() {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTeacherId(999L);
        request.setCategoryId(1L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.createCourse(request);
        });

        verify(userRepository, times(1)).findById(999L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void createCourse_UserNotTeacher() {
        // Given
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTeacherId(2L);
        request.setCategoryId(1L);

        User student = new User();
        student.setId(2L);
        student.setRole(Role.STUDENT);

        when(userRepository.findById(2L)).thenReturn(Optional.of(student));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            courseService.createCourse(request);
        });

        verify(userRepository, times(1)).findById(2L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void getCourseById_Success() {
        // Given
        when(courseRepository.findByIdWithModulesAndTeacher(1L)).thenReturn(Optional.of(testCourse));
        when(courseMapper.toResponse(testCourse)).thenReturn(testCourseResponse);

        // When
        CourseResponse response = courseService.getCourseById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(courseRepository, times(1)).findByIdWithModulesAndTeacher(1L);
    }

    @Test
    void getCourseById_NotFound() {
        // Given
        when(courseRepository.findByIdWithModulesAndTeacher(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.getCourseById(999L);
        });

        verify(courseRepository, times(1)).findByIdWithModulesAndTeacher(999L);
    }

    @Test
    void updateCourse_Success() {
        // Given
        UpdateCourseRequest request = new UpdateCourseRequest();
        request.setTitle("Updated Java Course");
        request.setDescription("Updated description");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);
        when(courseMapper.toResponse(any(Course.class))).thenReturn(testCourseResponse);

        // When
        CourseResponse response = courseService.updateCourse(1L, request);

        // Then
        assertNotNull(response);

        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void deleteCourse_Success() {
        // Given
        when(courseRepository.existsById(1L)).thenReturn(true);

        // When
        courseService.deleteCourse(1L);

        // Then
        verify(courseRepository, times(1)).existsById(1L);
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCourse_NotFound() {
        // Given
        when(courseRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.deleteCourse(999L);
        });

        verify(courseRepository, times(1)).existsById(999L);
        verify(courseRepository, never()).deleteById(anyLong());
    }

    @Test
    void getCoursesByTeacher_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(courseRepository.findByTeacherId(1L)).thenReturn(List.of(testCourse));
        when(courseMapper.toResponse(testCourse)).thenReturn(testCourseResponse);

        // When
        List<CourseResponse> responses = courseService.getCoursesByTeacher(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());

        verify(courseRepository, times(1)).findByTeacherId(1L);
    }
}
