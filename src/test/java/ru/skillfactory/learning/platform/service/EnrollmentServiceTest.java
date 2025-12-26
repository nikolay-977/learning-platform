package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.EnrollRequest;
import ru.skillfactory.learning.platform.dto.response.EnrollmentResponse;
import ru.skillfactory.learning.platform.entity.*;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.mapper.EnrollmentMapper;
import ru.skillfactory.learning.platform.repository.CourseRepository;
import ru.skillfactory.learning.platform.repository.EnrollmentRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.impl.EnrollmentServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private User testStudent;
    private User testTeacher;
    private Course testCourse;
    private Enrollment testEnrollment;
    private EnrollmentResponse testEnrollmentResponse;

    @BeforeEach
    void setUp() {
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setName("John Student");
        testStudent.setEmail("student@example.com");
        testStudent.setRole(Role.STUDENT);

        testTeacher = new User();
        testTeacher.setId(2L);
        testTeacher.setName("Jane Teacher");
        testTeacher.setRole(Role.TEACHER);

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Java Basics");
        testCourse.setTeacher(testTeacher);

        testEnrollment = new Enrollment();
        testEnrollment.setId(1L);
        testEnrollment.setStudent(testStudent);
        testEnrollment.setCourse(testCourse);
        testEnrollment.setEnrollDate(LocalDate.now());
        testEnrollment.setStatus(EnrollmentStatus.ACTIVE);

        testEnrollmentResponse = new EnrollmentResponse();
        testEnrollmentResponse.setId(1L);
        testEnrollmentResponse.setStudentId(1L);
        testEnrollmentResponse.setStudentName("John Student");
        testEnrollmentResponse.setCourseId(1L);
        testEnrollmentResponse.setCourseTitle("Java Basics");
        testEnrollmentResponse.setStatus(EnrollmentStatus.ACTIVE);
    }

    @Test
    void enrollStudent_Success() {
        // Given
        EnrollRequest request = new EnrollRequest();
        request.setStudentId(1L);
        request.setCourseId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);
        when(enrollmentMapper.toResponse(any(Enrollment.class))).thenReturn(testEnrollmentResponse);

        // When
        EnrollmentResponse response = enrollmentService.enrollStudent(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getStudentId());
        assertEquals(1L, response.getCourseId());

        verify(userRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(1L);
        verify(enrollmentRepository, times(1)).existsByStudentIdAndCourseId(1L, 1L);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_UserNotStudent() {
        // Given
        EnrollRequest request = new EnrollRequest();
        request.setStudentId(2L);
        request.setCourseId(1L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(testTeacher));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            enrollmentService.enrollStudent(request);
        });

        verify(userRepository, times(1)).findById(2L);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_AlreadyEnrolled() {
        // Given
        EnrollRequest request = new EnrollRequest();
        request.setStudentId(1L);
        request.setCourseId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            enrollmentService.enrollStudent(request);
        });

        verify(enrollmentRepository, times(1)).existsByStudentIdAndCourseId(1L, 1L);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void getEnrollmentById_Success() {
        // Given
        when(enrollmentRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testEnrollment));
        when(enrollmentMapper.toResponse(testEnrollment)).thenReturn(testEnrollmentResponse);

        // When
        EnrollmentResponse response = enrollmentService.getEnrollmentById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(enrollmentRepository, times(1)).findByIdWithDetails(1L);
    }

    @Test
    void cancelEnrollment_Success() {
        // Given
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(testEnrollment));

        // When
        enrollmentService.cancelEnrollment(1L);

        // Then
        verify(enrollmentRepository, times(1)).findById(1L);
        verify(enrollmentRepository, times(1)).delete(testEnrollment);
    }

    @Test
    void isStudentEnrolled_True() {
        // Given
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 1L)).thenReturn(true);

        // When
        boolean result = enrollmentService.isStudentEnrolled(1L, 1L);

        // Then
        assertTrue(result);
        verify(enrollmentRepository, times(1)).existsByStudentIdAndCourseId(1L, 1L);
    }

    @Test
    void updateEnrollmentStatus_Success() {
        // Given
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);
        when(enrollmentMapper.toResponse(any(Enrollment.class))).thenReturn(testEnrollmentResponse);

        // When
        EnrollmentResponse response = enrollmentService.updateEnrollmentStatus(1L, "COMPLETED");

        // Then
        assertNotNull(response);

        verify(enrollmentRepository, times(1)).findById(1L);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void updateEnrollmentStatus_InvalidStatus() {
        // Given
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(testEnrollment));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            enrollmentService.updateEnrollmentStatus(1L, "INVALID_STATUS");
        });

        verify(enrollmentRepository, times(1)).findById(1L);
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }
}
