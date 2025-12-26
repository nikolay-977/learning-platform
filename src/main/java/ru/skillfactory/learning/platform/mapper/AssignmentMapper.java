package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.AssignmentResponse;
import ru.skillfactory.learning.platform.entity.Assignment;

@Component
public class AssignmentMapper {

    public AssignmentResponse toResponse(Assignment assignment) {
        if (assignment == null) return null;

        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setDueDate(assignment.getDueDate());
        response.setMaxScore(assignment.getMaxScore());

        if (assignment.getLesson() != null) {
            response.setLessonId(assignment.getLesson().getId());
            response.setLessonTitle(assignment.getLesson().getTitle());
        }

        if (assignment.getSubmissions() != null) {
            response.setSubmissionCount(assignment.getSubmissions().size());
        }

        return response;
    }
}
