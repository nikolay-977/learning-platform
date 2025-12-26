package ru.skillfactory.learning.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skillfactory.learning.platform.dto.request.CreateCategoryRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCategoryRequest;
import ru.skillfactory.learning.platform.dto.response.CategoryResponse;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.service.CategoryService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private ObjectMapper objectMapper;
    private CategoryResponse categoryResponse;
    private CreateCategoryRequest createRequest;
    private UpdateCategoryRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("Programming");
        categoryResponse.setDescription("Programming courses");
        categoryResponse.setCourseCount(5);
        categoryResponse.setCreatedAt(LocalDateTime.now());
        categoryResponse.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateCategoryRequest();
        createRequest.setName("Programming");
        createRequest.setDescription("Programming courses");

        updateRequest = new UpdateCategoryRequest();
        updateRequest.setName("Updated Programming");
        updateRequest.setDescription("Updated description");
    }

    @Test
    void createCategory_Success() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CreateCategoryRequest.class)))
                .thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Category created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Programming"));
    }

    @Test
    void createCategory_ValidationFailed_EmptyName() throws Exception {
        // Arrange
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest();
        invalidRequest.setName("");
        invalidRequest.setDescription("Valid description");

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_ValidationFailed_NameTooShort() throws Exception {
        // Arrange
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest();
        invalidRequest.setName("A");
        invalidRequest.setDescription("Valid description");

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_ValidationFailed_NameTooLong() throws Exception {
        // Arrange
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest();
        invalidRequest.setName("A".repeat(101));
        invalidRequest.setDescription("Valid description");

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_ValidationFailed_DescriptionTooLong() throws Exception {
        // Arrange
        CreateCategoryRequest invalidRequest = new CreateCategoryRequest();
        invalidRequest.setName("Valid Name");
        invalidRequest.setDescription("A".repeat(501));

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategoryById_Success() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Programming"));
    }

    @Test
    void getCategoryDetailById_Success() throws Exception {
        // Arrange
        when(categoryService.getCategoryDetailById(1L)).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/1/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    void getAllCategories_Success() throws Exception {
        // Arrange
        List<CategoryResponse> categories = Arrays.asList(categoryResponse);
        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Programming"));
    }

    @Test
    void searchCategories_Success() throws Exception {
        // Arrange
        List<CategoryResponse> categories = Arrays.asList(categoryResponse);
        when(categoryService.searchCategories("prog")).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/search")
                        .param("keyword", "prog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name").value("Programming"));
    }

    @Test
    void getPopularCategories_Success() throws Exception {
        // Arrange
        List<CategoryResponse> categories = Arrays.asList(categoryResponse);
        when(categoryService.getPopularCategories(5)).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/popular")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void getPopularCategories_DefaultLimit() throws Exception {
        // Arrange
        List<CategoryResponse> categories = Arrays.asList(categoryResponse);
        when(categoryService.getPopularCategories(5)).thenReturn(categories);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    void getCategoryByName_Success() throws Exception {
        // Arrange
        when(categoryService.getCategoryByName("Programming")).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/name/Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Programming"));
    }

    @Test
    void getCategoryCount_Success() throws Exception {
        // Arrange
        when(categoryService.countCategories()).thenReturn(10L);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(10L));
    }

    @Test
    void checkCategoryExists_Success() throws Exception {
        // Arrange
        when(categoryService.existsByName("Programming")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/exists/Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void updateCategory_Success() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(1L), any(UpdateCategoryRequest.class)))
                .thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Category updated successfully"));
    }

    @Test
    void updateCategory_ValidationFailed() throws Exception {
        // Arrange
        UpdateCategoryRequest invalidRequest = new UpdateCategoryRequest();
        invalidRequest.setName("A"); // Too short

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategory_Success() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    @Test
    void createCategory_DuplicateName_ReturnsBadRequest() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CreateCategoryRequest.class)))
                .thenThrow(new IllegalArgumentException("Category with name 'Programming' already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategoryById_NotFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategory_NotFound() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(999L), any(UpdateCategoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCategory_WithCourses_ReturnsConflict() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("Cannot delete category 'Programming' with 5 associated courses"))
                .when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void updateCategory_DuplicateName_ThrowsException() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(1L), any(UpdateCategoryRequest.class)))
                .thenThrow(new IllegalArgumentException("Category with name 'Updated Programming' already exists"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCategoryByName_NotFound() throws Exception {
        // Arrange
        when(categoryService.getCategoryByName("NonExistent"))
                .thenThrow(new ResourceNotFoundException("Category not found with name: NonExistent"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/name/NonExistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCategories_EmptyList() throws Exception {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void searchCategories_EmptyResult() throws Exception {
        // Arrange
        when(categoryService.searchCategories("nonexistent")).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/search")
                        .param("keyword", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    void deleteCategory_NotFound() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Category not found with id: 999"))
                .when(categoryService).deleteCategory(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/categories/999"))
                .andExpect(status().isNotFound());
    }
}