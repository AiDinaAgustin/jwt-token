package com.example.jwt_token.repository;

import com.example.jwt_token.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
