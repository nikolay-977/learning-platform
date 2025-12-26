package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.LessonResponse;
import ru.skillfactory.learning.platform.entity.Lesson;

@Component
public class LessonMapper {

    public LessonResponse toResponse(Lesson lesson) {
        if (lesson == null) return null;

        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setTitle(lesson.getTitle());
        response.setContent(lesson.getContent());
        response.setVideoUrl(lesson.getVideoUrl());

        if (lesson.getModule() != null) {
            response.setModuleId(lesson.getModule().getId());
            response.setModuleTitle(lesson.getModule().getTitle());
        }

        if (lesson.getAssignments() != null) {
            response.setAssignmentCount(lesson.getAssignments().size());
        }

        return response;
    }
}
