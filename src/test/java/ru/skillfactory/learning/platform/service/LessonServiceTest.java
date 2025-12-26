package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.CreateLessonRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateLessonRequest;
import ru.skillfactory.learning.platform.dto.response.LessonResponse;
import ru.skillfactory.learning.platform.entity.Lesson;
import ru.skillfactory.learning.platform.entity.Module;
import ru.skillfactory.learning.platform.mapper.LessonMapper;
import ru.skillfactory.learning.platform.repository.LessonRepository;
import ru.skillfactory.learning.platform.repository.ModuleRepository;
import ru.skillfactory.learning.platform.service.impl.LessonServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LessonMapper lessonMapper;

    @InjectMocks
    private LessonServiceImpl lessonService;

    private Module testModule;
    private Lesson testLesson;
    private LessonResponse testLessonResponse;

    @BeforeEach
    void setUp() {
        testModule = new Module();
        testModule.setId(1L);
        testModule.setTitle("Module 1");

        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitle("Lesson 1");
        testLesson.setContent("Lesson content");
        testLesson.setVideoUrl("video.mp4");
        testLesson.setModule(testModule);

        testLessonResponse = new LessonResponse();
        testLessonResponse.setId(1L);
        testLessonResponse.setTitle("Lesson 1");
        testLessonResponse.setContent("Lesson content");
        testLessonResponse.setVideoUrl("video.mp4");
        testLessonResponse.setModuleId(1L);
        testLessonResponse.setModuleTitle("Module 1");
    }

    @Test
    void createLesson_Success() {
        // Given
        CreateLessonRequest request = new CreateLessonRequest();
        request.setTitle("Lesson 1");
        request.setContent("Lesson content");
        request.setVideoUrl("video.mp4");
        request.setModuleId(1L);

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);
        when(lessonMapper.toResponse(any(Lesson.class))).thenReturn(testLessonResponse);

        // When
        LessonResponse response = lessonService.createLesson(request);

        // Then
        assertNotNull(response);
        assertEquals("Lesson 1", response.getTitle());

        verify(moduleRepository, times(1)).findById(1L);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void getLessonById_Success() {
        // Given
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));
        when(lessonMapper.toResponse(testLesson)).thenReturn(testLessonResponse);

        // When
        LessonResponse response = lessonService.getLessonById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(lessonRepository, times(1)).findById(1L);
    }

    @Test
    void getLessonsByModule_Success() {
        // Given
        when(moduleRepository.existsById(1L)).thenReturn(true);
        when(lessonRepository.findByModuleId(1L)).thenReturn(List.of(testLesson));
        when(lessonMapper.toResponse(testLesson)).thenReturn(testLessonResponse);

        // When
        List<LessonResponse> responses = lessonService.getLessonsByModule(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());

        verify(lessonRepository, times(1)).findByModuleId(1L);
    }

    @Test
    void updateLesson_Success() {
        // Given
        UpdateLessonRequest request = new UpdateLessonRequest();
        request.setTitle("Updated Lesson");
        request.setContent("Updated content");

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);
        when(lessonMapper.toResponse(any(Lesson.class))).thenReturn(testLessonResponse);

        // When
        LessonResponse response = lessonService.updateLesson(1L, request);

        // Then
        assertNotNull(response);

        verify(lessonRepository, times(1)).findById(1L);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void deleteLesson_Success() {
        // Given
        when(lessonRepository.existsById(1L)).thenReturn(true);

        // When
        lessonService.deleteLesson(1L);

        // Then
        verify(lessonRepository, times(1)).existsById(1L);
        verify(lessonRepository, times(1)).deleteById(1L);
    }
}
