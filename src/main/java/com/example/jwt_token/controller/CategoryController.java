package com.example.jwt_token.controller;

import com.example.jwt_token.model.Category;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllcategories(
            @Param("keyword") String keyword,
            @RequestParam(defaultValue= "0") Integer page,
            @RequestParam(defaultValue= "10") Integer size) {
        var categories = categoryService.getAllCategories(keyword, page, size);
        return ResponseEntity.ok(new ApiResponse<>("Categories retrieved successfully", HttpStatus.OK, categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        var category = categoryService.getCategoryById(id);
        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Category not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Category retrieved successfully", HttpStatus.OK, category));
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category category) {
        var savedCategory = categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Category created successfully", HttpStatus.CREATED, savedCategory));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        var existingCategory = categoryService.getCategoryById(id);
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }
        existingCategory.setName(category.getName());
        var updatedCategory = categoryService.saveCategory(existingCategory);
        return ResponseEntity.ok(new ApiResponse<>("Category updated successfully", HttpStatus.OK, updatedCategory
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        var existingCategory = categoryService.getCategoryById(id);
        if (existingCategory == null) {
            return ResponseEntity.notFound().build();
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse<>("Category deleted successfully", HttpStatus.OK));
    }
}
