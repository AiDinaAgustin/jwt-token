package com.example.jwt_token.service;

import com.example.jwt_token.dto.ProductRequest;
import com.example.jwt_token.model.Product;
import com.example.jwt_token.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    // Get all Products without pagination
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get all Products with pagination, sorting, and searching
    public Page<Product> getListProducts(String keyword, Integer page, Integer limit, String sort, String order) {
        Sort sortBy = "DESC".equalsIgnoreCase(order)
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, limit, sortBy);

        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.search(keyword, pageable);
        }
        return productRepository.findAll(pageable);
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Save/New a new product
    public Product saveProduct(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setSlug(productRequest.getSlug());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCategory(productRequest.getCategory());
        return productRepository.save(product);
    }

    // Update an existing product
    public Product updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).map(existingProduct -> {
            existingProduct.setName(productRequest.getName());
            existingProduct.setSlug(productRequest.getSlug());
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
