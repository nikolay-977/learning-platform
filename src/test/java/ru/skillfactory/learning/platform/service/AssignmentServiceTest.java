package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.CreateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.response.AssignmentResponse;
import ru.skillfactory.learning.platform.entity.Assignment;
import ru.skillfactory.learning.platform.entity.Course;
import ru.skillfactory.learning.platform.entity.Lesson;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.AssignmentMapper;
import ru.skillfactory.learning.platform.repository.AssignmentRepository;
import ru.skillfactory.learning.platform.repository.EnrollmentRepository;
import ru.skillfactory.learning.platform.repository.LessonRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.impl.AssignmentServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private AssignmentMapper assignmentMapper;

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    private Lesson testLesson;
    private Module testModule;
    private Course testCourse;
    private User testTeacher;
    private Assignment testAssignment;
    private AssignmentResponse testAssignmentResponse;

    @BeforeEach
    void setUp() {
        testTeacher = new User();
        testTeacher.setId(2L);
        testTeacher.setName("Jane Teacher");
        testTeacher.setRole(Role.TEACHER);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Java Basics");
        testCourse.setTeacher(testTeacher);

        testModule = new Module();
        testModule.setId(1L);
        testModule.setTitle("Module 1");
        testModule.setCourse(testCourse);

        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitle("Lesson 1");
        testLesson.setModule(testModule);

        testAssignment = new Assignment();
        testAssignment.setId(1L);
        testAssignment.setTitle("Homework 1");
        testAssignment.setDescription("Complete exercises 1-5");
        testAssignment.setDueDate(LocalDate.now().plusDays(7));
        testAssignment.setMaxScore(100);
        testAssignment.setLesson(testLesson);

        testAssignmentResponse = new AssignmentResponse();
        testAssignmentResponse.setId(1L);
        testAssignmentResponse.setTitle("Homework 1");
        testAssignmentResponse.setDescription("Complete exercises 1-5");
        testAssignmentResponse.setDueDate(LocalDate.now().plusDays(7));
        testAssignmentResponse.setMaxScore(100);
        testAssignmentResponse.setLessonId(1L);
        testAssignmentResponse.setLessonTitle("Lesson 1");
    }

    @Test
    void createAssignment_Success() {
        // Given
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setTitle("Homework 1");
        request.setDescription("Complete exercises 1-5");
        request.setLessonId(1L);
        request.setDueDate(LocalDate.now().plusDays(7));
        request.setMaxScore(100);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(testAssignment);
        when(assignmentMapper.toResponse(any(Assignment.class))).thenReturn(testAssignmentResponse);

        // When
        AssignmentResponse response = assignmentService.createAssignment(request);

        // Then
        assertNotNull(response);
        assertEquals("Homework 1", response.getTitle());
        assertEquals(100, response.getMaxScore());

        verify(lessonRepository, times(1)).findById(1L);
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    void createAssignment_LessonNotFound() {
        // Given
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setLessonId(999L);

        when(lessonRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            assignmentService.createAssignment(request);
        });

        verify(lessonRepository, times(1)).findById(999L);
        verify(assignmentRepository, never()).save(any(Assignment.class));
    }

    @Test
    void getAssignmentById_Success() {
        // Given
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(testAssignment));
        when(assignmentMapper.toResponse(testAssignment)).thenReturn(testAssignmentResponse);

        // When
        AssignmentResponse response = assignmentService.getAssignmentById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(assignmentRepository, times(1)).findById(1L);
    }

    @Test
    void getAssignmentById_NotFound() {
        // Given
        when(assignmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            assignmentService.getAssignmentById(999L);
        });

        verify(assignmentRepository, times(1)).findById(999L);
    }

    @Test
    void getAssignmentsByLesson_Success() {
        // Given
        when(lessonRepository.existsById(1L)).thenReturn(true);
        when(assignmentRepository.findByLessonId(1L)).thenReturn(List.of(testAssignment));
        when(assignmentMapper.toResponse(testAssignment)).thenReturn(testAssignmentResponse);

        // When
        List<AssignmentResponse> responses = assignmentService.getAssignmentsByLesson(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());

        verify(assignmentRepository, times(1)).findByLessonId(1L);
    }

    @Test
    void getAssignmentsByLesson_LessonNotFound() {
        // Given
        when(lessonRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            assignmentService.getAssignmentsByLesson(999L);
        });

        verify(assignmentRepository, never()).findByLessonId(anyLong());
    }

    @Test
    void updateAssignment_Success() {
        // Given
        UpdateAssignmentRequest request = new UpdateAssignmentRequest();
        request.setTitle("Updated Homework");
        request.setDescription("Updated description");
        request.setMaxScore(90);

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(testAssignment));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(testAssignment);
        when(assignmentMapper.toResponse(any(Assignment.class))).thenReturn(testAssignmentResponse);

        // When
        AssignmentResponse response = assignmentService.updateAssignment(1L, request);

        // Then
        assertNotNull(response);

        verify(assignmentRepository, times(1)).findById(1L);
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    void updateAssignment_NotFound() {
        // Given
        UpdateAssignmentRequest request = new UpdateAssignmentRequest();

        when(assignmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            assignmentService.updateAssignment(999L, request);
        });

        verify(assignmentRepository, times(1)).findById(999L);
        verify(assignmentRepository, never()).save(any(Assignment.class));
    }

    @Test
    void updateAssignment_ChangeLesson() {
        // Given
        UpdateAssignmentRequest request = new UpdateAssignmentRequest();
        request.setLessonId(2L);

        Lesson newLesson = new Lesson();
        newLesson.setId(2L);
        newLesson.setTitle("Lesson 2");

        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(testAssignment));
        when(lessonRepository.findById(2L)).thenReturn(Optional.of(newLesson));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(testAssignment);
        when(assignmentMapper.toResponse(any(Assignment.class))).thenReturn(testAssignmentResponse);

        // When
        AssignmentResponse response = assignmentService.updateAssignment(1L, request);

        // Then
        assertNotNull(response);

        verify(lessonRepository, times(1)).findById(2L);
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
    }

    @Test
    void deleteAssignment_Success() {
        // Given
        when(assignmentRepository.existsById(1L)).thenReturn(true);

        // When
        assignmentService.deleteAssignment(1L);

        // Then
        verify(assignmentRepository, times(1)).existsById(1L);
        verify(assignmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteAssignment_NotFound() {
        // Given
        when(assignmentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            assignmentService.deleteAssignment(999L);
        });

        verify(assignmentRepository, times(1)).existsById(999L);
        verify(assignmentRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAssignmentsByTeacher_Success() {
        // Given
        User teacher = new User();
        teacher.setId(2L);
        teacher.setRole(Role.TEACHER);

        when(userRepository.findById(2L)).thenReturn(Optional.of(teacher));
        when(assignmentRepository.findAll()).thenReturn(List.of(testAssignment));
        when(assignmentMapper.toResponse(testAssignment)).thenReturn(testAssignmentResponse);

        // When
        List<AssignmentResponse> responses = assignmentService.getAssignmentsByTeacher(2L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());

        verify(assignmentRepository, times(1)).findAll();
    }

    @Test
    void getAssignmentsDueSoon_Success() {
        // Given
        Assignment dueSoonAssignment = new Assignment();
        dueSoonAssignment.setId(2L);
        dueSoonAssignment.setTitle("Due Soon");
        dueSoonAssignment.setDueDate(LocalDate.now().plusDays(2));
        dueSoonAssignment.setLesson(testLesson);

        Assignment farFutureAssignment = new Assignment();
        farFutureAssignment.setId(3L);
        farFutureAssignment.setTitle("Far Future");
        farFutureAssignment.setDueDate(LocalDate.now().plusDays(30));
        farFutureAssignment.setLesson(testLesson);

        when(assignmentRepository.findAll()).thenReturn(List.of(testAssignment, dueSoonAssignment, farFutureAssignment));
        when(assignmentMapper.toResponse(any(Assignment.class))).thenReturn(testAssignmentResponse);

        // When
        List<AssignmentResponse> responses = assignmentService.getAssignmentsDueSoon();

        // Then
        assertNotNull(responses);
        // Только dueSoonAssignment должен быть в результатах (текущий + 7 дней)

        verify(assignmentRepository, times(1)).findAll();
    }
}