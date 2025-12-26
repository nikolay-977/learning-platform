package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateModuleRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateModuleRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.ModuleDetailResponse;
import ru.skillfactory.learning.platform.dto.response.ModuleResponse;
import ru.skillfactory.learning.platform.service.ModuleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(
            @Valid @RequestBody CreateModuleRequest request) {

        ModuleResponse module = moduleService.createModule(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Module created successfully", module));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModuleById(@PathVariable Long id) {

        ModuleResponse module = moduleService.getModuleById(id);

        return ResponseEntity.ok(ApiResponse.success(module));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<ModuleDetailResponse>> getModuleDetailById(@PathVariable Long id) {

        ModuleDetailResponse module = moduleService.getModuleDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(module));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getModulesByCourse(
            @PathVariable Long courseId) {

        List<ModuleResponse> modules = moduleService.getModulesByCourse(courseId);

        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> getAllModules() {

        List<ModuleResponse> modules = moduleService.getAllModules();

        return ResponseEntity.ok(ApiResponse.success(modules));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateModuleRequest request) {

        ModuleResponse module = moduleService.updateModule(id, request);

        return ResponseEntity.ok(ApiResponse.success("Module updated successfully", module));
    }

    @PutMapping("/{id}/reorder/{newOrder}")
    public ResponseEntity<ApiResponse<ModuleResponse>> reorderModule(
            @PathVariable Long id,
            @PathVariable Integer newOrder) {

        ModuleResponse module = moduleService.reorderModule(id, newOrder);

        return ResponseEntity.ok(ApiResponse.success("Module reordered", module));
    }

    @PutMapping("/reorder")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> reorderModules(
            @RequestBody List<Long> moduleIdsInOrder) {

        List<ModuleResponse> modules = moduleService.reorderModules(moduleIdsInOrder);

        return ResponseEntity.ok(ApiResponse.success("Modules reordered", modules));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable Long id) {

        moduleService.deleteModule(id);

        return ResponseEntity.ok(ApiResponse.success("Module deleted successfully", null));
    }
}
