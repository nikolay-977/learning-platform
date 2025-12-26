package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.CreateLessonRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateLessonRequest;
import ru.skillfactory.learning.platform.dto.response.LessonDetailResponse;
import ru.skillfactory.learning.platform.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {

    LessonResponse createLesson(CreateLessonRequest request);

    LessonResponse getLessonById(Long id);

    LessonDetailResponse getLessonDetailById(Long id);

    List<LessonResponse> getLessonsByModule(Long moduleId);

    List<LessonResponse> getAllLessons();

    LessonResponse updateLesson(Long id, UpdateLessonRequest request);

    void deleteLesson(Long id);

    LessonResponse reorderLesson(Long id, Integer newOrder);
}
