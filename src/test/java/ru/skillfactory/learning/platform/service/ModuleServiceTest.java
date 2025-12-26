package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.CreateModuleRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateModuleRequest;
import ru.skillfactory.learning.platform.dto.response.ModuleResponse;
import ru.skillfactory.learning.platform.entity.Course;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.mapper.ModuleMapper;
import ru.skillfactory.learning.platform.repository.CourseRepository;
import ru.skillfactory.learning.platform.repository.ModuleRepository;
import ru.skillfactory.learning.platform.service.impl.ModuleServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ModuleMapper moduleMapper;

    @InjectMocks
    private ModuleServiceImpl moduleService;

    private Course testCourse;
    private Module testModule;
    private ModuleResponse testModuleResponse;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Java Basics");

        testModule = new Module();
        testModule.setId(1L);
        testModule.setTitle("Module 1");
        testModule.setDescription("First module");
        testModule.setOrderIndex(1);
        testModule.setCourse(testCourse);

        testModuleResponse = new ModuleResponse();
        testModuleResponse.setId(1L);
        testModuleResponse.setTitle("Module 1");
        testModuleResponse.setDescription("First module");
        testModuleResponse.setOrderIndex(1);
        testModuleResponse.setCourseId(1L);
        testModuleResponse.setCourseTitle("Java Basics");
    }

    @Test
    void createModule_Success() {
        // Given
        CreateModuleRequest request = new CreateModuleRequest();
        request.setTitle("Module 1");
        request.setDescription("First module");
        request.setCourseId(1L);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(moduleRepository.findByCourseId(1L)).thenReturn(List.of());
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);
        when(moduleMapper.toResponse(any(Module.class))).thenReturn(testModuleResponse);

        // When
        ModuleResponse response = moduleService.createModule(request);

        // Then
        assertNotNull(response);
        assertEquals("Module 1", response.getTitle());

        verify(courseRepository, times(1)).findById(1L);
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void createModule_WithOrderIndex() {
        // Given
        CreateModuleRequest request = new CreateModuleRequest();
        request.setTitle("Module 2");
        request.setCourseId(1L);
        request.setOrderIndex(2);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);
        when(moduleMapper.toResponse(any(Module.class))).thenReturn(testModuleResponse);

        // When
        ModuleResponse response = moduleService.createModule(request);

        // Then
        assertNotNull(response);

        verify(courseRepository, times(1)).findById(1L);
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void getModulesByCourse_Success() {
        // Given
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(moduleRepository.findByCourseId(1L)).thenReturn(List.of(testModule));
        when(moduleMapper.toResponse(testModule)).thenReturn(testModuleResponse);

        // When
        List<ModuleResponse> responses = moduleService.getModulesByCourse(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());

        verify(moduleRepository, times(1)).findByCourseId(1L);
    }

    @Test
    void updateModule_Success() {
        // Given
        UpdateModuleRequest request = new UpdateModuleRequest();
        request.setTitle("Updated Module");
        request.setDescription("Updated description");

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(moduleRepository.save(any(Module.class))).thenReturn(testModule);
        when(moduleMapper.toResponse(any(Module.class))).thenReturn(testModuleResponse);

        // When
        ModuleResponse response = moduleService.updateModule(1L, request);

        // Then
        assertNotNull(response);

        verify(moduleRepository, times(1)).findById(1L);
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test
    void reorderModule_Success() {
        // Given
        Module module2 = new Module();
        module2.setId(2L);
        module2.setTitle("Module 2");
        module2.setOrderIndex(2);
        module2.setCourse(testCourse);

        // Используем изменяемый список
        List<Module> modules = new ArrayList<>();
        modules.add(testModule);
        modules.add(module2);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(moduleRepository.findByCourseId(1L)).thenReturn(modules);
        when(moduleRepository.saveAll(anyList())).thenReturn(modules);
        when(moduleMapper.toResponse(testModule)).thenReturn(testModuleResponse);

        // When
        ModuleResponse response = moduleService.reorderModule(1L, 2);

        // Then
        assertNotNull(response);

        verify(moduleRepository, times(1)).findById(1L);
        verify(moduleRepository, times(1)).findByCourseId(1L);
        verify(moduleRepository, times(1)).saveAll(anyList());
    }
}