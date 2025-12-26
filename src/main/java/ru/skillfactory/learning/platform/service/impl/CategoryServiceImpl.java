package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateCategoryRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCategoryRequest;
import ru.skillfactory.learning.platform.dto.response.CategoryResponse;
import ru.skillfactory.learning.platform.entity.Category;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.CategoryMapper;
import ru.skillfactory.learning.platform.repository.CategoryRepository;
import ru.skillfactory.learning.platform.service.CategoryService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        log.info("Creating category with name: {}", request.getName());

        // Проверяем уникальность имени
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        Category category = new Category();
        category.setName(request.getName());

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created with id: {}", savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#id")
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Fetching category by id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getAllCategories() {
        log.debug("Fetching all categories");

        List<Category> categories = categoryRepository.findAll(
                Sort.by(Sort.Direction.ASC, "name")
        );

        return categories.stream()
                .map(categoryMapper::toResponse)  // Исправлено: categoryMapper вместо CategoryMapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> searchCategories(String keyword) {
        log.debug("Searching categories with keyword: {}", keyword);

        List<Category> categories = categoryRepository.findByNameContainingIgnoreCase(keyword);

        return categories.stream()
                .map(categoryMapper::toResponse)  // Исправлено: categoryMapper вместо CategoryMapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", key = "#id")
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Проверяем уникальность имени, если оно изменено
        if (request.getName() != null &&
                !request.getName().equals(category.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }

        if (request.getName() != null) {
            category.setName(request.getName());
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated with id: {}", updatedCategory.getId());

        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", key = "#id")
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        Category category = categoryRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Проверяем, есть ли связанные курсы
        if (!category.getCourses().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Cannot delete category '%s' with %d associated courses. " +
                                    "Please reassign or delete courses first.",
                            category.getName(), category.getCourses().size())
            );
        }

        categoryRepository.delete(category);  // Должно быть здесь
        log.info("Category deleted with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCategories() {
        return categoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#name")
    public CategoryResponse getCategoryByName(String name) {
        log.debug("Fetching category by name: {}", name);

        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoryDetails", key = "#id")
    public CategoryResponse getCategoryDetailById(Long id) {
        log.debug("Fetching category detail by id: {}", id);

        Category category = categoryRepository.findByIdWithCourses(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "popularCategories", key = "#limit")
    public List<CategoryResponse> getPopularCategories(int limit) {
        log.debug("Fetching {} popular categories", limit);

        List<Category> allCategories = categoryRepository.findAllWithCourseCount();

        return allCategories.stream()
                .sorted(Comparator.comparingInt(c ->
                        c.getCourses() != null ? -c.getCourses().size() : 0)) // Добавьте проверку на null
                .limit(limit)
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Получает категорию или создает новую, если не существует
     */
    @Transactional
    public Category getOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category newCategory = new Category();
                    newCategory.setName(name);
                    return categoryRepository.save(newCategory);
                });
    }

    /**
     * Обновляет количество курсов в категории
     */
    @Transactional
    public void updateCourseCount(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Количество курсов будет автоматически обновляться через связь
        log.debug("Category '{}' now has {} courses",
                category.getName(),
                category.getCourses() != null ? category.getCourses().size() : 0);
    }
}