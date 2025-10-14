package com.example.jwt_token.controller;

import com.example.jwt_token.dto.ProductRequest;
import com.example.jwt_token.model.Category;
import com.example.jwt_token.model.Product;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.response.PaginatedResult;
import com.example.jwt_token.service.CategoryService;
import com.example.jwt_token.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    // Digunakan untuk List Products tanpa pagination
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        var products = productService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse<>("Products retrieved successfully", HttpStatus.OK, products));
    }

    // Digunakan untuk List Producs menggunakan pagination
        @GetMapping("/list")
        public ResponseEntity<?> getListProducts(
                @RequestParam("keyword") String keyword,
                @RequestParam(defaultValue = "0") Integer page,
                @RequestParam(required = false) Long categoryId,
                @RequestParam(defaultValue = "10") Integer limit,
                @RequestParam(defaultValue = "id") String sort,
                @RequestParam(defaultValue = "ASC") String order) {

            var products = productService.getListProducts(keyword, categoryId, page, limit, sort, order);

            PaginatedResult<?> paginated = new PaginatedResult<>(products);

            return ResponseEntity.ok(new ApiResponse<>("Products retrieved successfully", HttpStatus.OK, paginated));
        }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        var product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Product not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Product retrieved successfully", HttpStatus.OK, product));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryService.getCategoryById(product.getCategory().getId());
            if (category == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("Invalid category ID", HttpStatus.BAD_REQUEST));
            }
            product.setCategory(category);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Category is required", HttpStatus.BAD_REQUEST));
        }
        var savedProduct = productService.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Product created successfully", HttpStatus.CREATED, savedProduct));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryService.getCategoryById(product.getCategory().getId());
            if (category == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>("Invalid category ID", HttpStatus.BAD_REQUEST));
            }
            product.setCategory(category);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Category is required", HttpStatus.BAD_REQUEST));
        }
        var updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(new ApiResponse<>("Product updated successfully", HttpStatus.OK, updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully", HttpStatus.OK));
    }
}
