package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.SubmitAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateSubmissionRequest;
import ru.skillfactory.learning.platform.dto.response.SubmissionResponse;
import ru.skillfactory.learning.platform.entity.Assignment;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.Submission;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.mapper.SubmissionMapper;
import ru.skillfactory.learning.platform.repository.AssignmentRepository;
import ru.skillfactory.learning.platform.repository.SubmissionRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.impl.SubmissionServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubmissionMapper submissionMapper;

    @InjectMocks
    private SubmissionServiceImpl submissionService;

    private User testStudent;
    private Assignment testAssignment;
    private Submission testSubmission;
    private SubmissionResponse testSubmissionResponse;

    @BeforeEach
    void setUp() {
        testStudent = new User();
        testStudent.setId(1L);
        testStudent.setName("John Student");
        testStudent.setEmail("student@example.com");
        testStudent.setRole(Role.STUDENT);

        testAssignment = new Assignment();
        testAssignment.setId(1L);
        testAssignment.setTitle("Homework 1");
        testAssignment.setMaxScore(100);
        testAssignment.setDueDate(LocalDate.now().plusDays(7));

        testSubmission = new Submission();
        testSubmission.setId(1L);
        testSubmission.setStudent(testStudent);
        testSubmission.setAssignment(testAssignment);
        testSubmission.setContent("My submission");
        testSubmission.setSubmittedAt(LocalDateTime.now());

        testSubmissionResponse = new SubmissionResponse();
        testSubmissionResponse.setId(1L);
        testSubmissionResponse.setContent("My submission");
        testSubmissionResponse.setStudentId(1L);
        testSubmissionResponse.setStudentName("John Student");
        testSubmissionResponse.setAssignmentId(1L);
        testSubmissionResponse.setAssignmentTitle("Homework 1");
    }

    @Test
    void submitAssignment_Success() {
        // Given
        SubmitAssignmentRequest request = new SubmitAssignmentRequest();
        request.setAssignmentId(1L);
        request.setContent("My submission");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(testAssignment));
        when(submissionRepository.findByAssignmentIdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(submissionRepository.save(any(Submission.class))).thenReturn(testSubmission);
        when(submissionMapper.toResponse(any(Submission.class))).thenReturn(testSubmissionResponse);

        // When
        SubmissionResponse response = submissionService.submitAssignment(1L, request);

        // Then
        assertNotNull(response);
        assertEquals("My submission", response.getContent());

        verify(userRepository, times(1)).findById(1L);
        verify(assignmentRepository, times(1)).findById(1L);
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    void submitAssignment_UserNotStudent() {
        // Given
        SubmitAssignmentRequest request = new SubmitAssignmentRequest();
        request.setAssignmentId(1L);

        User teacher = new User();
        teacher.setId(2L);
        teacher.setRole(Role.TEACHER);

        when(userRepository.findById(2L)).thenReturn(Optional.of(teacher));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            submissionService.submitAssignment(2L, request);
        });

        verify(userRepository, times(1)).findById(2L);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    void submitAssignment_DeadlinePassed() {
        // Given
        SubmitAssignmentRequest request = new SubmitAssignmentRequest();
        request.setAssignmentId(1L);
        request.setContent("My submission");

        Assignment overdueAssignment = new Assignment();
        overdueAssignment.setId(1L);
        overdueAssignment.setTitle("Homework 1");
        overdueAssignment.setDueDate(LocalDate.now().minusDays(1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(overdueAssignment));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            submissionService.submitAssignment(1L, request);
        });

        verify(assignmentRepository, times(1)).findById(1L);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    void submitAssignment_AlreadySubmitted() {
        // Given
        SubmitAssignmentRequest request = new SubmitAssignmentRequest();
        request.setAssignmentId(1L);
        request.setContent("My submission");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(testAssignment));
        when(submissionRepository.findByAssignmentIdAndStudentId(1L, 1L)).thenReturn(Optional.of(testSubmission));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            submissionService.submitAssignment(1L, request);
        });

        verify(submissionRepository, times(1)).findByAssignmentIdAndStudentId(1L, 1L);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    void getSubmissionById_Success() {
        // Given
        when(submissionRepository.findByIdWithAssignment(1L)).thenReturn(Optional.of(testSubmission));
        when(submissionMapper.toResponse(testSubmission)).thenReturn(testSubmissionResponse);

        // When
        SubmissionResponse response = submissionService.getSubmissionById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(submissionRepository, times(1)).findByIdWithAssignment(1L);
    }

    @Test
    void updateSubmission_UpdateContentBeforeDeadline() {
        // Given
        UpdateSubmissionRequest request = new UpdateSubmissionRequest();
        request.setContent("Updated content");

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(testSubmission);
        when(submissionMapper.toResponse(any(Submission.class))).thenReturn(testSubmissionResponse);

        // When
        SubmissionResponse response = submissionService.updateSubmission(1L, request);

        // Then
        assertNotNull(response);

        verify(submissionRepository, times(1)).findById(1L);
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    void updateSubmission_GradeSubmission() {
        // Given
        UpdateSubmissionRequest request = new UpdateSubmissionRequest();
        request.setScore(85);
        request.setFeedback("Good work!");

        when(submissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(testSubmission);
        when(submissionMapper.toResponse(any(Submission.class))).thenReturn(testSubmissionResponse);

        // When
        SubmissionResponse response = submissionService.updateSubmission(1L, request);

        // Then
        assertNotNull(response);

        verify(submissionRepository, times(1)).findById(1L);
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    void gradeSubmission_Success() {
        // Given
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));
        when(submissionRepository.save(any(Submission.class))).thenReturn(testSubmission);
        when(submissionMapper.toResponse(any(Submission.class))).thenReturn(testSubmissionResponse);

        // When
        SubmissionResponse response = submissionService.gradeSubmission(1L, 90, "Excellent!");

        // Then
        assertNotNull(response);

        verify(submissionRepository, times(1)).findById(1L);
        verify(submissionRepository, times(1)).save(any(Submission.class));
    }

    @Test
    void gradeSubmission_InvalidScore() {
        // Given
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(testSubmission));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            submissionService.gradeSubmission(1L, 150, "Too high!");
        });

        verify(submissionRepository, times(1)).findById(1L);
        verify(submissionRepository, never()).save(any(Submission.class));
    }

    @Test
    void hasStudentSubmitted_True() {
        // Given
        when(submissionRepository.findByAssignmentIdAndStudentId(1L, 1L))
                .thenReturn(Optional.of(testSubmission));

        // When
        boolean result = submissionService.hasStudentSubmitted(1L, 1L);

        // Then
        assertTrue(result);
        verify(submissionRepository, times(1)).findByAssignmentIdAndStudentId(1L, 1L);
    }

    @Test
    void hasStudentSubmitted_False() {
        // Given
        when(submissionRepository.findByAssignmentIdAndStudentId(1L, 1L))
                .thenReturn(Optional.empty());

        // When
        boolean result = submissionService.hasStudentSubmitted(1L, 1L);

        // Then
        assertFalse(result);
        verify(submissionRepository, times(1)).findByAssignmentIdAndStudentId(1L, 1L);
    }
}
