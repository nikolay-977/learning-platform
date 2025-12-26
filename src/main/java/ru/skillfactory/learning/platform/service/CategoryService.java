package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.CreateCategoryRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCategoryRequest;
import ru.skillfactory.learning.platform.dto.response.CategoryResponse;
import ru.skillfactory.learning.platform.entity.Category;

import java.util.List;

public interface CategoryService {

    /**
     * Создает новую категорию
     */
    CategoryResponse createCategory(CreateCategoryRequest request);

    /**
     * Получает категорию по ID
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * Получает все категории
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Ищет категории по ключевому слову
     */
    List<CategoryResponse> searchCategories(String keyword);

    /**
     * Получает сущность категории по ID
     */
    Category getCategoryEntity(Long id);

    /**
     * Обновляет категорию
     */
    CategoryResponse updateCategory(Long id, UpdateCategoryRequest request);

    /**
     * Удаляет категорию
     */
    void deleteCategory(Long id);

    /**
     * Подсчитывает количество категорий
     */
    long countCategories();

    /**
     * Проверяет существование категории по имени
     */
    boolean existsByName(String name);

    /**
     * Получает категорию по имени
     */
    CategoryResponse getCategoryByName(String name);

    /**
     * Получает детальную информацию о категории с курсами
     */
    CategoryResponse getCategoryDetailById(Long id);

    /**
     * Получает популярные категории (с наибольшим количеством курсов)
     */
    List<CategoryResponse> getPopularCategories(int limit);
}
