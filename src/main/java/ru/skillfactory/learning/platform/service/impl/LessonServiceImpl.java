package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateLessonRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateLessonRequest;
import ru.skillfactory.learning.platform.dto.response.LessonDetailResponse;
import ru.skillfactory.learning.platform.dto.response.LessonResponse;
import ru.skillfactory.learning.platform.entity.Lesson;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.LessonMapper;
import ru.skillfactory.learning.platform.repository.LessonRepository;
import ru.skillfactory.learning.platform.repository.ModuleRepository;
import ru.skillfactory.learning.platform.service.LessonService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional
    public LessonResponse createLesson(CreateLessonRequest request) {
        log.info("Creating lesson: {}", request.getTitle());

        // Проверяем модуль
        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", request.getModuleId()));

        // Создаем урок
        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setModule(module);

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Lesson created with ID: {}", savedLesson.getId());

        return lessonMapper.toResponse(savedLesson);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long id) {
        log.info("Getting lesson by ID: {}", id);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonDetailResponse getLessonDetailById(Long id) {
        log.info("Getting lesson detail by ID: {}", id);

        Lesson lesson = lessonRepository.findByIdWithAssignments(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        return toDetailResponse(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByModule(Long moduleId) {
        log.info("Getting lessons for module ID: {}", moduleId);

        if (!moduleRepository.existsById(moduleId)) {
            throw new ResourceNotFoundException("Module", "id", moduleId);
        }

        List<Lesson> lessons = lessonRepository.findByModuleId(moduleId);

        return lessons.stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getAllLessons() {
        log.info("Getting all lessons");

        List<Lesson> lessons = lessonRepository.findAll();

        return lessons.stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long id, UpdateLessonRequest request) {
        log.info("Updating lesson with ID: {}", id);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        if (request.getTitle() != null) {
            lesson.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            lesson.setContent(request.getContent());
        }
        if (request.getVideoUrl() != null) {
            lesson.setVideoUrl(request.getVideoUrl());
        }
        if (request.getModuleId() != null) {
            Module module = moduleRepository.findById(request.getModuleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Module", "id", request.getModuleId()));
            lesson.setModule(module);
        }

        Lesson updatedLesson = lessonRepository.save(lesson);
        log.info("Lesson updated with ID: {}", updatedLesson.getId());

        return lessonMapper.toResponse(updatedLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        log.info("Deleting lesson with ID: {}", id);

        if (!lessonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lesson", "id", id);
        }

        lessonRepository.deleteById(id);
        log.info("Lesson deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public LessonResponse reorderLesson(Long id, Integer newOrder) {
        log.info("Reordering lesson ID: {} to position: {}", id, newOrder);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));

        // В этой простой реализации мы просто обновляем урок
        // В реальном приложении нужно было бы переупорядочить все уроки в модуле

        return lessonMapper.toResponse(lesson);
    }

    private LessonDetailResponse toDetailResponse(Lesson lesson) {
        LessonDetailResponse response = new LessonDetailResponse();
        response.setId(lesson.getId());
        response.setTitle(lesson.getTitle());
        response.setContent(lesson.getContent());
        response.setVideoUrl(lesson.getVideoUrl());

        if (lesson.getModule() != null) {
            response.setModuleId(lesson.getModule().getId());
            response.setModuleTitle(lesson.getModule().getTitle());

            if (lesson.getModule().getCourse() != null) {
                response.setCourseId(lesson.getModule().getCourse().getId());
                response.setCourseTitle(lesson.getModule().getCourse().getTitle());
            }
        }

        // Задания
        if (lesson.getAssignments() != null) {
            response.setAssignments(lesson.getAssignments().stream()
                    .map(assignment -> {
                        LessonDetailResponse.AssignmentResponse assignmentResponse =
                                new LessonDetailResponse.AssignmentResponse();
                        assignmentResponse.setId(assignment.getId());
                        assignmentResponse.setTitle(assignment.getTitle());
                        assignmentResponse.setDescription(assignment.getDescription());
                        assignmentResponse.setDueDate(assignment.getDueDate());
                        assignmentResponse.setMaxScore(assignment.getMaxScore());
                        return assignmentResponse;
                    })
                    .collect(Collectors.toList()));
        }

        return response;
    }
}
