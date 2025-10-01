package com.example.jwt_token.service;

import com.example.jwt_token.model.Product;
import com.example.jwt_token.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    // Get all products
    public List<Product> getAllProducts(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.search(keyword);
        }
        return productRepository.findAll();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Save/New a new product
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Delete a product by ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
