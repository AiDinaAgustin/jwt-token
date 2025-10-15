package com.example.jwt_token.service;

import com.example.jwt_token.dto.CategoryRequest;
import com.example.jwt_token.model.Category;
import com.example.jwt_token.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Get all Categories without pagination
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get all Categories with pagination, sorting, and searching
    public Page<Category> getListCategories(String keyword, Integer page, Integer limit, String sort, String order) {
        Sort sortBy = "DESC".equalsIgnoreCase(order)
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, limit, sortBy);


        if (keyword != null && !keyword.isEmpty()) {
            return categoryRepository.search(keyword, pageable);
        }
            return categoryRepository.findAll(pageable);
    }

    // Get Category by ID
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    // Create or Update Category
    public Category createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();

        category.setName(categoryRequest.getName());
        category.setSlug(categoryRequest.getSlug());
        category.setDescription(categoryRequest.getDescription());
        return categoryRepository.save(category);
    }

    // Update Category
    public Category updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category != null) {
            category.setName(categoryRequest.getName());
            category.setSlug(categoryRequest.getSlug());
            category.setDescription(categoryRequest.getDescription());
            return categoryRepository.save(category);
        }
        return null;
    }

    // Delete Category by ID
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
