package ru.skillfactory.learning.platform.mapper;

import org.springframework.stereotype.Component;
import ru.skillfactory.learning.platform.dto.response.CategoryResponse;
import ru.skillfactory.learning.platform.dto.response.CategoryDetailResponse;
import ru.skillfactory.learning.platform.entity.Category;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    private final CourseMapper courseMapper;

    // Конструктор для внедрения зависимости
    public CategoryMapper(CourseMapper courseMapper) {
        this.courseMapper = courseMapper;
    }

    /**
     * Конвертирует сущность Category в CategoryResponse
     */
    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        response.setCourseCount(calculateCourseCount(category));

        return response;
    }

    /**
     * Конвертирует сущность Category в CategoryDetailResponse
     */
    public CategoryDetailResponse toDetailResponse(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDetailResponse response = new CategoryDetailResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        // Если есть курсы, конвертируем их
        if (category.getCourses() != null && !category.getCourses().isEmpty()) {
            response.setCourses(courseMapper.toResponseList(category.getCourses()));
        } else {
            response.setCourses(Collections.emptyList());
        }

        return response;
    }

    /**
     * Конвертирует список сущностей Category в список CategoryResponse
     */
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        if (categories == null) {
            return null;
        }

        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Конвертирует список сущностей Category в список CategoryDetailResponse
     */
    public List<CategoryDetailResponse> toDetailResponseList(List<Category> categories) {
        if (categories == null) {
            return null;
        }

        return categories.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * Конвертирует CategoryResponse обратно в сущность Category (для обновления)
     */
    public Category toEntity(CategoryResponse response) {
        if (response == null) {
            return null;
        }

        Category category = new Category();
        category.setId(response.getId());
        category.setName(response.getName());
        category.setDescription(response.getDescription());

        return category;
    }

    /**
     * Обновляет существующую сущность Category из CategoryResponse
     */
    public void updateEntityFromResponse(Category category, CategoryResponse response) {
        if (category == null || response == null) {
            return;
        }

        category.setName(response.getName());
        category.setDescription(response.getDescription());
    }

    /**
     * Подсчитывает количество курсов в категории
     */
    private int calculateCourseCount(Category category) {
        return category.getCourses() != null ? category.getCourses().size() : 0;
    }
}