package com.example.jwt_token.controller;

import com.example.jwt_token.model.Category;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.response.PaginatedResult;
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

    // Digunakan untuk List Categories tanpa pagination
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        var categories = categoryService.getAllCategories();
        return ResponseEntity.ok(new ApiResponse<>("Categories retrieved successfully", HttpStatus.OK, categories));
    }

    // Digunakan untuk List Categories menggunakan pagination
    @GetMapping("/list")
    public ResponseEntity<?> getListCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue= "0") Integer page,
            @RequestParam(defaultValue= "10") Integer limit,
            @RequestParam(defaultValue= "id") String sort,
            @RequestParam(defaultValue= "ASC") String order) {

        var categories = categoryService.getListCategories(keyword, page, limit, sort, order);

        PaginatedResult<?> paginated = new PaginatedResult<>(categories);

        return ResponseEntity.ok(new ApiResponse<>("Categories retrieved successfully", HttpStatus.OK, paginated));
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
