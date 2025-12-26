package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.EnrollRequest;
import ru.skillfactory.learning.platform.dto.response.EnrollmentResponse;
import ru.skillfactory.learning.platform.entity.*;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.EnrollmentMapper;
import ru.skillfactory.learning.platform.repository.CourseRepository;
import ru.skillfactory.learning.platform.repository.EnrollmentRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.EnrollmentService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    @Transactional
    public EnrollmentResponse enrollStudent(EnrollRequest request) {
        log.info("Enrolling student ID: {} to course ID: {}", request.getStudentId(), request.getCourseId());

        // Проверяем студента
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getStudentId()));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        // Проверяем курс
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        // Проверяем, не записан ли уже студент
        if (enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId())) {
            throw new BadRequestException("Student is already enrolled in this course");
        }

        // Создаем запись о зачислении
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Student enrolled with enrollment ID: {}", savedEnrollment.getId());

        return enrollmentMapper.toResponse(savedEnrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {
        log.info("Getting enrollment by ID: {}", id);

        Enrollment enrollment = enrollmentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));

        return enrollmentMapper.toResponse(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByStudent(Long studentId) {
        log.info("Getting enrollments for student ID: {}", studentId);

        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("User", "id", studentId);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);

        return enrollments.stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        log.info("Getting enrollments for course ID: {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);

        return enrollments.stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollments() {
        log.info("Getting all enrollments");

        List<Enrollment> enrollments = enrollmentRepository.findAll();

        return enrollments.stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(Long id, String status) {
        log.info("Updating enrollment status for ID: {} to {}", id, status);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));

        try {
            EnrollmentStatus newStatus = EnrollmentStatus.valueOf(status.toUpperCase());
            enrollment.setStatus(newStatus);

            Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
            log.info("Enrollment status updated for ID: {}", id);

            return enrollmentMapper.toResponse(updatedEnrollment);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid enrollment status: " + status);
        }
    }

    @Override
    @Transactional
    public void cancelEnrollment(Long id) {
        log.info("Canceling enrollment with ID: {}", id);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", id));

        enrollmentRepository.delete(enrollment);
        log.info("Enrollment canceled with ID: {}", id);
    }

    @Override
    @Transactional
    public void cancelEnrollmentByStudentAndCourse(Long studentId, Long courseId) {
        log.info("Canceling enrollment for student ID: {} and course ID: {}", studentId, courseId);

        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Enrollment", "studentId and courseId", studentId + ", " + courseId));

        enrollmentRepository.delete(enrollment);
        log.info("Enrollment canceled for student ID: {} and course ID: {}", studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getActiveEnrollmentsCount(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return (int) enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                .count();
    }

    @Override
    @Transactional
    public EnrollmentResponse completeCourse(Long enrollmentId) {
        log.info("Marking course as completed for enrollment ID: {}", enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        enrollment.setStatus(EnrollmentStatus.COMPLETED);

        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Course marked as completed for enrollment ID: {}", enrollmentId);

        return enrollmentMapper.toResponse(updatedEnrollment);
    }
}
