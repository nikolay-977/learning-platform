package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.skillfactory.learning.platform.dto.request.CreateCategoryRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCategoryRequest;
import ru.skillfactory.learning.platform.dto.response.CategoryResponse;
import ru.skillfactory.learning.platform.entity.Category;
import ru.skillfactory.learning.platform.entity.Course;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.CategoryMapper;
import ru.skillfactory.learning.platform.repository.CategoryRepository;
import ru.skillfactory.learning.platform.service.impl.CategoryServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private Category categoryWithCourses;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        // Setup basic category
        category = new Category();
        category.setId(1L);
        category.setName("Programming");
        category.setDescription("Programming courses");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        // Setup category with courses
        categoryWithCourses = new Category();
        categoryWithCourses.setId(2L);
        categoryWithCourses.setName("Category with Courses");
        categoryWithCourses.setCourses(new ArrayList<>());

        Course course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Java Course");

        Course course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Python Course");

        categoryWithCourses.getCourses().add(course1);
        categoryWithCourses.getCourses().add(course2);

        // Setup requests
        createRequest = new CreateCategoryRequest();
        createRequest.setName("Programming");
        createRequest.setDescription("Programming courses");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Programming");
        updateRequest.setDescription("Updated description");

        // Setup response
        categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Programming");
        categoryResponse.setDescription("Programming courses");
        categoryResponse.setCourseCount(0);
        categoryResponse.setCreatedAt(LocalDateTime.now());
        categoryResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createCategory_Success() {
        // Arrange
        when(categoryRepository.existsByName("Programming")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Programming", result.getName());
        verify(categoryRepository).existsByName("Programming");
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        // Arrange
        when(categoryRepository.existsByName("Programming")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(createRequest);
        });
        assertEquals("Category with name 'Programming' already exists", exception.getMessage());
        verify(categoryRepository).existsByName("Programming");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(999L);
        });
        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository).findById(999L);
        verify(categoryMapper, never()).toResponse(any(Category.class));
    }

    @Test
    void getAllCategories_Success() {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAll(any(Sort.class))).thenReturn(categories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        List<CategoryResponse> results = categoryService.getAllCategories();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(categoryRepository).findAll(any(Sort.class));
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getAllCategories_EmptyList() {
        // Arrange
        when(categoryRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList());

        // Act
        List<CategoryResponse> results = categoryService.getAllCategories();

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(categoryRepository).findAll(any(Sort.class));
        verify(categoryMapper, never()).toResponse(any(Category.class));
    }

    @Test
    void searchCategories_Success() {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findByNameContainingIgnoreCase("prog")).thenReturn(categories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        List<CategoryResponse> results = categoryService.searchCategories("prog");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(categoryRepository).findByNameContainingIgnoreCase("prog");
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void searchCategories_EmptyResult() {
        // Arrange
        when(categoryRepository.findByNameContainingIgnoreCase("nonexistent")).thenReturn(Arrays.asList());

        // Act
        List<CategoryResponse> results = categoryService.searchCategories("nonexistent");

        // Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(categoryRepository).findByNameContainingIgnoreCase("nonexistent");
        verify(categoryMapper, never()).toResponse(any(Category.class));
    }

    @Test
    void updateCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Updated Programming")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).existsByName("Updated Programming");
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void updateCategory_NotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.updateCategory(1L, updateRequest);
        });
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).existsByName(anyString());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_DuplicateName_ThrowsException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Updated Programming")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(1L, updateRequest);
        });
        assertEquals("Category with name 'Updated Programming' already exists", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).existsByName("Updated Programming");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_OnlyNameUpdated() {
        // Arrange
        UpdateCategoryRequest nameOnlyRequest = new UpdateCategoryRequest();
        nameOnlyRequest.setName("New Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("New Name")).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, nameOnlyRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).existsByName("New Name");
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void updateCategory_OnlyDescriptionUpdated() {
        // Arrange
        UpdateCategoryRequest descOnlyRequest = new UpdateCategoryRequest();
        descOnlyRequest.setDescription("New Description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, descOnlyRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).existsByName(anyString());
        verify(categoryRepository).save(category);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void deleteCategory_Success() {
        // Arrange
        when(categoryRepository.findByIdWithCourses(1L)).thenReturn(Optional.of(category));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).findByIdWithCourses(1L);
        verify(categoryRepository).delete(category);
        // Удалите verify(categoryRepository).existsById(1L);
    }

    @Test
    void deleteCategory_NotFound() {
        // Arrange
        when(categoryRepository.findByIdWithCourses(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(1L);
        });
        verify(categoryRepository).findByIdWithCourses(1L);
        verify(categoryRepository, never()).existsById(anyLong());
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_WithCourses_ThrowsException() {
        // Arrange
        when(categoryRepository.findByIdWithCourses(2L)).thenReturn(Optional.of(categoryWithCourses));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            categoryService.deleteCategory(2L);
        });

        assertTrue(exception.getMessage().contains("Cannot delete category"));

        // Проверяем только вызов findByIdWithCourses
        verify(categoryRepository).findByIdWithCourses(2L);
        // Убираем проверку existsById, так как она не должна вызываться при исключении
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void getCategoryByName_Success() {
        // Arrange
        when(categoryRepository.findByName("Programming")).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.getCategoryByName("Programming");

        // Assert
        assertNotNull(result);
        assertEquals("Programming", result.getName());
        verify(categoryRepository).findByName("Programming");
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getCategoryByName_NotFound() {
        // Arrange
        when(categoryRepository.findByName("NonExistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryByName("NonExistent");
        });
        verify(categoryRepository).findByName("NonExistent");
        verify(categoryMapper, never()).toResponse(any(Category.class));
    }

    @Test
    void existsByName_Success() {
        // Arrange
        when(categoryRepository.existsByName("Programming")).thenReturn(true);

        // Act
        boolean exists = categoryService.existsByName("Programming");

        // Assert
        assertTrue(exists);
        verify(categoryRepository).existsByName("Programming");
    }

    @Test
    void existsByName_False() {
        // Arrange
        when(categoryRepository.existsByName("NonExistent")).thenReturn(false);

        // Act
        boolean exists = categoryService.existsByName("NonExistent");

        // Assert
        assertFalse(exists);
        verify(categoryRepository).existsByName("NonExistent");
    }

    @Test
    void countCategories_Success() {
        // Arrange
        when(categoryRepository.count()).thenReturn(5L);

        // Act
        long count = categoryService.countCategories();

        // Assert
        assertEquals(5L, count);
        verify(categoryRepository).count();
    }

    @Test
    void getCategoryDetailById_Success() {
        // Arrange
        category.setCourses(new ArrayList<>()); // Инициализируйте список курсов

        when(categoryRepository.findByIdWithCourses(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        CategoryResponse result = categoryService.getCategoryDetailById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Programming", result.getName());
        verify(categoryRepository).findByIdWithCourses(1L);
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getPopularCategories_Success() {
        // Arrange
        CategoryResponse popularResponse = new CategoryResponse();
        popularResponse.setId(2L);
        popularResponse.setName("Popular Category");
        popularResponse.setCourseCount(2);

        List<Category> categories = Arrays.asList(categoryWithCourses, category);
        when(categoryRepository.findAllWithCourseCount()).thenReturn(categories);
        when(categoryMapper.toResponse(categoryWithCourses)).thenReturn(popularResponse);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        List<CategoryResponse> results = categoryService.getPopularCategories(2);

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        // Первая категория должна быть с большим количеством курсов
        assertEquals("Popular Category", results.get(0).getName());
        verify(categoryRepository).findAllWithCourseCount();
        verify(categoryMapper, times(2)).toResponse(any(Category.class));
    }

    @Test
    void getPopularCategories_LimitExceedsAvailable() {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAllWithCourseCount()).thenReturn(categories);
        when(categoryMapper.toResponse(category)).thenReturn(categoryResponse);

        // Act
        List<CategoryResponse> results = categoryService.getPopularCategories(10);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(categoryRepository).findAllWithCourseCount();
        verify(categoryMapper).toResponse(category);
    }

    @Test
    void getOrCreateCategory_CreatesNew() {
        // Arrange
        when(categoryRepository.findByName("New Category")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category result = categoryService.getOrCreateCategory("New Category");

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findByName("New Category");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void getOrCreateCategory_ReturnsExisting() {
        // Arrange
        when(categoryRepository.findByName("Existing Category")).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getOrCreateCategory("Existing Category");

        // Assert
        assertNotNull(result);
        assertEquals(category, result);
        verify(categoryRepository).findByName("Existing Category");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getCategoryEntity_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryEntity(1L);

        // Assert
        assertNotNull(result);
        assertEquals(category, result);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryEntity_NotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryEntity(999L);
        });
        verify(categoryRepository).findById(999L);
    }
}