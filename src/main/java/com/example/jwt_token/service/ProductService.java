package com.example.jwt_token.service;

import com.example.jwt_token.dto.ProductRequest;
import com.example.jwt_token.model.Product;
import com.example.jwt_token.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    // Get all products
    public List<Product> getAllProducts(String keyword, Integer page, Integer size, String sortBy, Boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.search(keyword, pageable).getContent();
        }
        return productRepository.findAll(pageable).getContent();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Save/New a new product
    public Product saveProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(productRequest.getName());
            existingProduct.setPrice(productRequest.getPrice());
            existingProduct.setQuantity(productRequest.getQuantity());
            existingProduct.setCategory(productRequest.getCategory());
            return productRepository.save(existingProduct);
        }).orElse(null);
    }

    // Delete a product by ID
    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return true;
        }).orElse(false);
    }
}
