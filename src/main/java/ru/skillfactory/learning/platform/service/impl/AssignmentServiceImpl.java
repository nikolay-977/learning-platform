package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.response.AssignmentDetailResponse;
import ru.skillfactory.learning.platform.dto.response.AssignmentResponse;
import ru.skillfactory.learning.platform.entity.Assignment;
import ru.skillfactory.learning.platform.entity.Enrollment;
import ru.skillfactory.learning.platform.entity.Lesson;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.AssignmentMapper;
import ru.skillfactory.learning.platform.repository.AssignmentRepository;
import ru.skillfactory.learning.platform.repository.EnrollmentRepository;
import ru.skillfactory.learning.platform.repository.LessonRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.AssignmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentMapper assignmentMapper;

    @Override
    @Transactional
    public AssignmentResponse createAssignment(CreateAssignmentRequest request) {
        log.info("Creating assignment: {}", request.getTitle());

        // Проверяем урок
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", request.getLessonId()));

        // Создаем задание
        Assignment assignment = new Assignment();
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxScore(request.getMaxScore());
        assignment.setLesson(lesson);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment created with ID: {}", savedAssignment.getId());

        return assignmentMapper.toResponse(savedAssignment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getAssignmentById(Long id) {
        log.info("Getting assignment by ID: {}", id);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        return assignmentMapper.toResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentDetailResponse getAssignmentDetailById(Long id) {
        log.info("Getting assignment detail by ID: {}", id);

        Assignment assignment = assignmentRepository.findByIdWithSubmissions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        return toDetailResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByLesson(Long lessonId) {
        log.info("Getting assignments for lesson ID: {}", lessonId);

        if (!lessonRepository.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson", "id", lessonId);
        }

        List<Assignment> assignments = assignmentRepository.findByLessonId(lessonId);

        return assignments.stream()
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAllAssignments() {
        log.info("Getting all assignments");

        List<Assignment> assignments = assignmentRepository.findAll();

        return assignments.stream()
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(Long id, UpdateAssignmentRequest request) {
        log.info("Updating assignment with ID: {}", id);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));

        if (request.getTitle() != null) {
            assignment.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            assignment.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            assignment.setDueDate(request.getDueDate());
        }
        if (request.getMaxScore() != null) {
            assignment.setMaxScore(request.getMaxScore());
        }
        if (request.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", request.getLessonId()));
            assignment.setLesson(lesson);
        }

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment updated with ID: {}", updatedAssignment.getId());

        return assignmentMapper.toResponse(updatedAssignment);
    }

    @Override
    @Transactional
    public void deleteAssignment(Long id) {
        log.info("Deleting assignment with ID: {}", id);

        if (!assignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Assignment", "id", id);
        }

        assignmentRepository.deleteById(id);
        log.info("Assignment deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsDueSoon() {
        log.info("Getting assignments due soon");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate nextWeek = LocalDate.now().plusDays(7);

        List<Assignment> allAssignments = assignmentRepository.findAll();

        return allAssignments.stream()
                .filter(assignment -> assignment.getDueDate() != null)
                .filter(assignment -> !assignment.getDueDate().isBefore(LocalDate.now()))
                .filter(assignment -> assignment.getDueDate().isBefore(nextWeek))
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByStudent(Long studentId) {
        log.info("Getting assignments for student ID: {}", studentId);

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        // Получаем курсы студента
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        List<Long> courseIds = enrollments.stream()
                .map(enrollment -> enrollment.getCourse().getId())
                .toList();

        // Получаем все задания из этих курсов
        List<Assignment> allAssignments = assignmentRepository.findAll();

        return allAssignments.stream()
                .filter(assignment -> {
                    if (assignment.getLesson() == null ||
                            assignment.getLesson().getModule() == null ||
                            assignment.getLesson().getModule().getCourse() == null) {
                        return false;
                    }
                    Long courseId = assignment.getLesson().getModule().getCourse().getId();
                    return courseIds.contains(courseId);
                })
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId) {
        log.info("Getting assignments for teacher ID: {}", teacherId);

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", teacherId));

        List<Assignment> allAssignments = assignmentRepository.findAll();

        return allAssignments.stream()
                .filter(assignment -> {
                    if (assignment.getLesson() == null ||
                            assignment.getLesson().getModule() == null ||
                            assignment.getLesson().getModule().getCourse() == null) {
                        return false;
                    }
                    return assignment.getLesson().getModule().getCourse().getTeacher().getId().equals(teacherId);
                })
                .map(assignmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    private AssignmentDetailResponse toDetailResponse(Assignment assignment) {
        AssignmentDetailResponse response = new AssignmentDetailResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setDueDate(assignment.getDueDate());
        response.setMaxScore(assignment.getMaxScore());

        if (assignment.getLesson() != null) {
            response.setLessonId(assignment.getLesson().getId());
            response.setLessonTitle(assignment.getLesson().getTitle());

            if (assignment.getLesson().getModule() != null) {
                response.setModuleId(assignment.getLesson().getModule().getId());
                response.setModuleTitle(assignment.getLesson().getModule().getTitle());

                if (assignment.getLesson().getModule().getCourse() != null) {
                    response.setCourseId(assignment.getLesson().getModule().getCourse().getId());
                    response.setCourseTitle(assignment.getLesson().getModule().getCourse().getTitle());
                }
            }
        }

        // Решения
        if (assignment.getSubmissions() != null) {
            response.setSubmissionsCount(assignment.getSubmissions().size());
            response.setSubmittedCount((int) assignment.getSubmissions().stream()
                    .filter(submission -> submission.getSubmittedAt() != null)
                    .count());
            response.setGradedCount((int) assignment.getSubmissions().stream()
                    .filter(submission -> submission.getScore() != null)
                    .count());
        }

        return response;
    }
}
