package com.example.jwt_token.repository;

import com.example.jwt_token.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
