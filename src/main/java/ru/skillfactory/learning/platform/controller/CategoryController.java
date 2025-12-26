package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateCategoryRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateCategoryRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.CategoryResponse;
import ru.skillfactory.learning.platform.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse category = categoryService.createCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {

        CategoryResponse category = categoryService.getCategoryById(id);

        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryDetailById(@PathVariable Long id) {

        CategoryResponse category = categoryService.getCategoryDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {

        List<CategoryResponse> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> searchCategories(
            @RequestParam String keyword) {

        List<CategoryResponse> categories = categoryService.searchCategories(keyword);

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getPopularCategories(
            @RequestParam(defaultValue = "5") int limit) {

        List<CategoryResponse> categories = categoryService.getPopularCategories(limit);

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByName(
            @PathVariable String name) {

        CategoryResponse category = categoryService.getCategoryByName(name);

        return ResponseEntity.ok(ApiResponse.success(category));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getCategoryCount() {

        long count = categoryService.countCategories();

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/exists/{name}")
    public ResponseEntity<ApiResponse<Boolean>> checkCategoryExists(
            @PathVariable String name) {

        boolean exists = categoryService.existsByName(name);

        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        CategoryResponse category = categoryService.updateCategory(id, request);

        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
