package com.example.jwt_token.service;

import com.example.jwt_token.model.Category;
import com.example.jwt_token.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Get All Categories
    public List<Category> getAllCategories(String keyword, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword != null && !keyword.isEmpty()) {
            return categoryRepository.search(keyword, pageable);
        }
            return categoryRepository.findAll(pageable).getContent();
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
