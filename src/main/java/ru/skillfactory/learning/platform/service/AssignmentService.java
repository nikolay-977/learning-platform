package ru.skillfactory.learning.platform.service;

import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateAssignmentRequest;
import ru.skillfactory.learning.platform.dto.response.AssignmentDetailResponse;
import ru.skillfactory.learning.platform.dto.response.AssignmentResponse;

import java.util.List;

public interface AssignmentService {

    AssignmentResponse createAssignment(CreateAssignmentRequest request);

    AssignmentResponse getAssignmentById(Long id);

    AssignmentDetailResponse getAssignmentDetailById(Long id);

    List<AssignmentResponse> getAssignmentsByLesson(Long lessonId);

    List<AssignmentResponse> getAllAssignments();

    AssignmentResponse updateAssignment(Long id, UpdateAssignmentRequest request);

    void deleteAssignment(Long id);

    List<AssignmentResponse> getAssignmentsDueSoon();

    List<AssignmentResponse> getAssignmentsByStudent(Long studentId);

    List<AssignmentResponse> getAssignmentsByTeacher(Long teacherId);
}
