package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.CreateModuleRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateModuleRequest;
import ru.skillfactory.learning.platform.dto.response.ModuleDetailResponse;
import ru.skillfactory.learning.platform.dto.response.ModuleResponse;

import java.util.List;

public interface ModuleService {

    ModuleResponse createModule(CreateModuleRequest request);

    ModuleResponse getModuleById(Long id);

    ModuleDetailResponse getModuleDetailById(Long id);

    List<ModuleResponse> getModulesByCourse(Long courseId);

    List<ModuleResponse> getAllModules();

    ModuleResponse updateModule(Long id, UpdateModuleRequest request);

    void deleteModule(Long id);

    ModuleResponse reorderModule(Long id, Integer newOrder);

    List<ModuleResponse> reorderModules(List<Long> moduleIdsInOrder);
}
