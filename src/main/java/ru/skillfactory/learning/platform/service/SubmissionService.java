package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.SubmitAssignmentRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateSubmissionRequest;
import ru.skillfactory.learning.platform.dto.response.SubmissionDetailResponse;
import ru.skillfactory.learning.platform.dto.response.SubmissionResponse;

import java.util.List;

public interface SubmissionService {

    SubmissionResponse submitAssignment(Long studentId, SubmitAssignmentRequest request);

    SubmissionResponse getSubmissionById(Long id);

    SubmissionDetailResponse getSubmissionDetailById(Long id);

    List<SubmissionResponse> getSubmissionsByAssignment(Long assignmentId);

    List<SubmissionResponse> getSubmissionsByStudent(Long studentId);

    List<SubmissionResponse> getAllSubmissions();

    SubmissionResponse updateSubmission(Long id, UpdateSubmissionRequest request);

    void deleteSubmission(Long id);

    SubmissionResponse gradeSubmission(Long id, Integer score, String feedback);

    boolean hasStudentSubmitted(Long studentId, Long assignmentId);

    List<SubmissionResponse> getUngradedSubmissions();

    List<SubmissionResponse> getSubmissionsByCourse(Long courseId);
}
