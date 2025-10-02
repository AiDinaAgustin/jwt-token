package com.example.jwt_token.service;

import com.example.jwt_token.model.Category;
import com.example.jwt_token.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Get All Categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get Category by ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // Create or Update Category
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Delete Category by ID
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
