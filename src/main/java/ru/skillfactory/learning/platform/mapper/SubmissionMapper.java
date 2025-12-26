package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.SubmissionResponse;
import ru.skillfactory.learning.platform.entity.Submission;

@Component
public class SubmissionMapper {

    public SubmissionResponse toResponse(Submission submission) {
        if (submission == null) return null;

        SubmissionResponse response = new SubmissionResponse();
        response.setId(submission.getId());
        response.setContent(submission.getContent());
        response.setSubmittedAt(submission.getSubmittedAt());
        response.setScore(submission.getScore());
        response.setFeedback(submission.getFeedback());

        if (submission.getAssignment() != null) {
            response.setAssignmentId(submission.getAssignment().getId());
            response.setAssignmentTitle(submission.getAssignment().getTitle());
        }

        if (submission.getStudent() != null) {
            response.setStudentId(submission.getStudent().getId());
            response.setStudentName(submission.getStudent().getName());
        }

        return response;
    }
}
