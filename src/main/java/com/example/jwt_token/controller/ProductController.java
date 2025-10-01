package com.example.jwt_token.controller;

import com.example.jwt_token.model.Product;
import com.example.jwt_token.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        var product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        var savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        var existingProduct = productService.getProductById(id);
        if (existingProduct == null) {
            return ResponseEntity.notFound().build();
        }
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        var updatedProduct = productService.saveProduct(existingProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@RequestParam Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
