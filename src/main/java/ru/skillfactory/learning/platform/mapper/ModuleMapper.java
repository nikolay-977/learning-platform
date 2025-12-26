package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.ModuleResponse;
import ru.skillfactory.learning.platform.entity.Module;

@Component
public class ModuleMapper {

    public ModuleResponse toResponse(Module module) {
        if (module == null) return null;

        ModuleResponse response = new ModuleResponse();
        response.setId(module.getId());
        response.setTitle(module.getTitle());
        response.setDescription(module.getDescription());
        response.setOrderIndex(module.getOrderIndex());

        if (module.getCourse() != null) {
            response.setCourseId(module.getCourse().getId());
            response.setCourseTitle(module.getCourse().getTitle());
        }

        if (module.getLessons() != null) {
            response.setLessonCount(module.getLessons().size());
        }

        response.setHasQuiz(module.getQuiz() != null);

        return response;
    }
}
