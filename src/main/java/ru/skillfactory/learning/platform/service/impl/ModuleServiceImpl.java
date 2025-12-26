package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateModuleRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateModuleRequest;
import ru.skillfactory.learning.platform.dto.response.ModuleDetailResponse;
import ru.skillfactory.learning.platform.dto.response.ModuleResponse;
import ru.skillfactory.learning.platform.entity.Course;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.ModuleMapper;
import ru.skillfactory.learning.platform.repository.CourseRepository;
import ru.skillfactory.learning.platform.repository.ModuleRepository;
import ru.skillfactory.learning.platform.service.ModuleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ModuleMapper moduleMapper;

    @Override
    @Transactional
    public ModuleResponse createModule(CreateModuleRequest request) {
        log.info("Creating module: {}", request.getTitle());

        // Проверяем курс
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        // Определяем порядковый номер
        Integer orderIndex = request.getOrderIndex();
        if (orderIndex == null) {
            List<Module> existingModules = moduleRepository.findByCourseId(request.getCourseId());
            orderIndex = existingModules.size() + 1;
        }

        // Создаем модуль
        Module module = new Module();
        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        module.setOrderIndex(orderIndex);
        module.setCourse(course);

        Module savedModule = moduleRepository.save(module);
        log.info("Module created with ID: {}", savedModule.getId());

        return moduleMapper.toResponse(savedModule);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleResponse getModuleById(Long id) {
        log.info("Getting module by ID: {}", id);

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));

        return moduleMapper.toResponse(module);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleDetailResponse getModuleDetailById(Long id) {
        log.info("Getting module detail by ID: {}", id);

        Module module = moduleRepository.findByIdWithLessons(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));

        return toDetailResponse(module);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> getModulesByCourse(Long courseId) {
        log.info("Getting modules for course ID: {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course", "id", courseId);
        }

        List<Module> modules = moduleRepository.findByCourseId(courseId);

        return modules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ModuleResponse> getAllModules() {
        log.info("Getting all modules");

        List<Module> modules = moduleRepository.findAll();

        return modules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ModuleResponse updateModule(Long id, UpdateModuleRequest request) {
        log.info("Updating module with ID: {}", id);

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));

        if (request.getTitle() != null) {
            module.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            module.setDescription(request.getDescription());
        }
        if (request.getOrderIndex() != null) {
            module.setOrderIndex(request.getOrderIndex());
        }

        Module updatedModule = moduleRepository.save(module);
        log.info("Module updated with ID: {}", updatedModule.getId());

        return moduleMapper.toResponse(updatedModule);
    }

    @Override
    @Transactional
    public void deleteModule(Long id) {
        log.info("Deleting module with ID: {}", id);

        if (!moduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Module", "id", id);
        }

        moduleRepository.deleteById(id);
        log.info("Module deleted with ID: {}", id);
    }

    @Override
    @Transactional
    public ModuleResponse reorderModule(Long id, Integer newOrder) {
        log.info("Reordering module ID: {} to position: {}", id, newOrder);

        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Module", "id", id));

        Long courseId = module.getCourse().getId();
        List<Module> modules = moduleRepository.findByCourseId(courseId);

        // Обновляем порядок всех модулей
        modules.remove(module);
        modules.add(newOrder - 1, module);

        for (int i = 0; i < modules.size(); i++) {
            modules.get(i).setOrderIndex(i + 1);
        }

        moduleRepository.saveAll(modules);

        return moduleMapper.toResponse(module);
    }

    @Override
    @Transactional
    public List<ModuleResponse> reorderModules(List<Long> moduleIdsInOrder) {
        log.info("Reordering modules: {}", moduleIdsInOrder);

        List<Module> modules = moduleRepository.findAllById(moduleIdsInOrder);

        if (modules.size() != moduleIdsInOrder.size()) {
            throw new ResourceNotFoundException("Some modules not found");
        }

        // Проверяем, что все модули из одного курса
        Long courseId = modules.get(0).getCourse().getId();
        boolean sameCourse = modules.stream()
                .allMatch(module -> module.getCourse().getId().equals(courseId));

        if (!sameCourse) {
            throw new IllegalArgumentException("All modules must be from the same course");
        }

        // Обновляем порядок
        for (int i = 0; i < moduleIdsInOrder.size(); i++) {
            Long moduleId = moduleIdsInOrder.get(i);
            Module module = modules.stream()
                    .filter(m -> m.getId().equals(moduleId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Module", "id", moduleId));

            module.setOrderIndex(i + 1);
        }

        List<Module> updatedModules = moduleRepository.saveAll(modules);

        return updatedModules.stream()
                .map(moduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    private ModuleDetailResponse toDetailResponse(Module module) {
        ModuleDetailResponse response = new ModuleDetailResponse();
        response.setId(module.getId());
        response.setTitle(module.getTitle());
        response.setDescription(module.getDescription());
        response.setOrderIndex(module.getOrderIndex());

        if (module.getCourse() != null) {
            response.setCourseId(module.getCourse().getId());
            response.setCourseTitle(module.getCourse().getTitle());
        }

        // Уроки
        if (module.getLessons() != null) {
            response.setLessons(module.getLessons().stream()
                    .map(lesson -> {
                        ModuleDetailResponse.LessonResponse lessonResponse = new ModuleDetailResponse.LessonResponse();
                        lessonResponse.setId(lesson.getId());
                        lessonResponse.setTitle(lesson.getTitle());
                        lessonResponse.setContent(lesson.getContent());
                        lessonResponse.setVideoUrl(lesson.getVideoUrl());
                        return lessonResponse;
                    })
                    .collect(Collectors.toList()));
        }

        // Тест
        if (module.getQuiz() != null) {
            response.setHasQuiz(true);
            response.setQuizId(module.getQuiz().getId());
            response.setQuizTitle(module.getQuiz().getTitle());
        }

        return response;
    }
}
