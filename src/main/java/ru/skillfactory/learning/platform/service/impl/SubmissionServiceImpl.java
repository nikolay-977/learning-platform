package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.SubmitAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateSubmissionRequest;
import ru.skillfactory.learning.platform.dto.response.SubmissionDetailResponse;
import ru.skillfactory.learning.platform.dto.response.SubmissionResponse;
import ru.skillfactory.learning.platform.entity.Assignment;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.Submission;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.SubmissionMapper;
import ru.skillfactory.learning.platform.repository.AssignmentRepository;
import ru.skillfactory.learning.platform.repository.SubmissionRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.SubmissionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final SubmissionMapper submissionMapper;

    @Override
    @Transactional
    public SubmissionResponse submitAssignment(Long studentId, SubmitAssignmentRequest request) {
        log.info("Student {} submitting assignment {}", studentId, request.getAssignmentId());

        // Проверяем студента
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        // Проверяем задание
        Assignment assignment = assignmentRepository.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", request.getAssignmentId()));

        // Проверяем дедлайн
        if (assignment.getDueDate() != null && assignment.getDueDate().isBefore(LocalDateTime.now().toLocalDate())) {
            throw new BadRequestException("Assignment deadline has passed");
        }

        // Проверяем, не отправлял ли уже студент решение
        if (hasStudentSubmitted(studentId, request.getAssignmentId())) {
            throw new BadRequestException("Student has already submitted this assignment");
        }

        // Создаем решение
        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.setContent(request.getContent());
        submission.setSubmittedAt(LocalDateTime.now());

        Submission savedSubmission = submissionRepository.save(submission);
        log.info("Submission created with ID: {}", savedSubmission.getId());

        return submissionMapper.toResponse(savedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(Long id) {
        log.info("Getting submission by ID: {}", id);

        Submission submission = submissionRepository.findByIdWithAssignment(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));

        return submissionMapper.toResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionDetailResponse getSubmissionDetailById(Long id) {
        log.info("Getting submission detail by ID: {}", id);

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));

        // Явно загружаем связанные данные
        if (submission.getAssignment() != null && submission.getAssignment().getId() != null) {
            submission.setAssignment(assignmentRepository.findById(submission.getAssignment().getId()).orElse(null));
        }

        if (submission.getStudent() != null && submission.getStudent().getId() != null) {
            submission.setStudent(userRepository.findById(submission.getStudent().getId()).orElse(null));
        }

        return toDetailResponse(submission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId) {
        log.info("Getting submissions for assignment ID: {}", assignmentId);

        if (!assignmentRepository.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Assignment", "id", assignmentId);
        }

        List<Submission> submissions = submissionRepository.findByAssignmentId(assignmentId);

        return submissions.stream()
                .map(submissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByStudent(Long studentId) {
        log.info("Getting submissions for student ID: {}", studentId);

        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("User", "id", studentId);
        }

        List<Submission> submissions = submissionRepository.findByStudentId(studentId);

        return submissions.stream()
                .map(submissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getAllSubmissions() {
        log.info("Getting all submissions");

        List<Submission> submissions = submissionRepository.findAll();

        return submissions.stream()
                .map(submissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubmissionResponse updateSubmission(Long id, UpdateSubmissionRequest request) {
        log.info("Updating submission with ID: {}", id);

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));

        // Студенты могут обновлять только контент до дедлайна
        if (request.getContent() != null) {
            if (submission.getAssignment().getDueDate() != null &&
                    submission.getAssignment().getDueDate().isBefore(LocalDateTime.now().toLocalDate())) {
                throw new BadRequestException("Cannot update submission after deadline");
            }
            submission.setContent(request.getContent());
        }

        // Только преподаватель может выставлять оценки и feedback
        if (request.getScore() != null || request.getFeedback() != null) {
            // В реальном приложении здесь была бы проверка роли пользователя
            if (request.getScore() != null) {
                if (request.getScore() < 0 || request.getScore() > submission.getAssignment().getMaxScore()) {
                    throw new BadRequestException("Score must be between 0 and " + submission.getAssignment().getMaxScore());
                }
                submission.setScore(request.getScore());
            }
            if (request.getFeedback() != null) {
                submission.setFeedback(request.getFeedback());
            }
        }

        Submission updatedSubmission = submissionRepository.save(submission);
        log.info("Submission updated with ID: {}", updatedSubmission.getId());

        return submissionMapper.toResponse(updatedSubmission);
    }

    @Override
    @Transactional
    public void deleteSubmission(Long id) {
        log.info("Deleting submission with ID: {}", id);

        if (!submissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Submission", "id", id);
        }

        submissionRepository.deleteById(id);
        log.info("Submission deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public SubmissionResponse gradeSubmission(Long id, Integer score, String feedback) {
        log.info("Grading submission ID: {} with score: {}", id, score);

        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", id));

        if (score < 0 || score > submission.getAssignment().getMaxScore()) {
            throw new BadRequestException("Score must be between 0 and " + submission.getAssignment().getMaxScore());
        }

        submission.setScore(score);
        submission.setFeedback(feedback);

        Submission gradedSubmission = submissionRepository.save(submission);
        log.info("Submission graded with ID: {}", id);

        return submissionMapper.toResponse(gradedSubmission);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasStudentSubmitted(Long studentId, Long assignmentId) {
        return submissionRepository.findByAssignmentIdAndStudentId(assignmentId, studentId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getUngradedSubmissions() {
        log.info("Getting ungraded submissions");

        List<Submission> allSubmissions = submissionRepository.findAll();

        return allSubmissions.stream()
                .filter(submission -> submission.getScore() == null)
                .map(submissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByCourse(Long courseId) {
        log.info("Getting submissions for course ID: {}", courseId);

        List<Submission> submissions = submissionRepository.findByCourseId(courseId);

        return submissions.stream()
                .map(submissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    private SubmissionDetailResponse toDetailResponse(Submission submission) {
        SubmissionDetailResponse response = new SubmissionDetailResponse();
        response.setId(submission.getId());
        response.setContent(submission.getContent());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setScore(submission.getScore());
        response.setFeedback(submission.getFeedback());

        if (submission.getStudent() != null) {
            response.setStudentId(submission.getStudent().getId());
            response.setStudentName(submission.getStudent().getName());
            response.setStudentEmail(submission.getStudent().getEmail());
        }

        if (submission.getAssignment() != null) {
            response.setAssignmentId(submission.getAssignment().getId());
            response.setAssignmentTitle(submission.getAssignment().getTitle());
            response.setMaxScore(submission.getAssignment().getMaxScore());
            response.setDueDate(submission.getAssignment().getDueDate());

            if (submission.getAssignment().getLesson() != null &&
                    submission.getAssignment().getLesson().getModule() != null &&
                    submission.getAssignment().getLesson().getModule().getCourse() != null) {

                response.setCourseId(submission.getAssignment().getLesson().getModule().getCourse().getId());
                response.setCourseTitle(submission.getAssignment().getLesson().getModule().getCourse().getTitle());
            }
        }

        // Проверяем, просрочено ли задание
        if (submission.getAssignment() != null &&
                submission.getAssignment().getDueDate() != null &&
                submission.getSubmittedAt() != null) {

            LocalDateTime dueDateTime = submission.getAssignment().getDueDate().atTime(23, 59, 59);
            response.setLate(submission.getSubmittedAt().isAfter(dueDateTime));
        }

        return response;
    }
}
