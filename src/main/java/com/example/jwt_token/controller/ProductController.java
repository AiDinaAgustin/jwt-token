package com.example.jwt_token.controller;

import com.example.jwt_token.dto.ProductRequest;
import com.example.jwt_token.model.Category;
import com.example.jwt_token.model.Product;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.response.PaginatedResult;
import com.example.jwt_token.service.CategoryService;
import com.example.jwt_token.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.parameters.RequestBody; // ← yang dari io.swagger!

import java.io.ByteArrayInputStream;

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
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        try {
            Product savedProduct = productService.createProduct(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Product created successfully", HttpStatus.CREATED, savedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to create product", HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
        try {
            Product updatedProduct = productService.updateProduct(id, productRequest);
            if (updatedProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Product not found", HttpStatus.NOT_FOUND));
            }
            return ResponseEntity.ok(new ApiResponse<>("Product updated successfully", HttpStatus.OK, updatedProduct));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update product", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse<>("Product deleted successfully", HttpStatus.OK));
    }

    // Export products to Excel
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> exportProducts() {
        try {
            ByteArrayInputStream in = productService.exportProductsExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=products.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to export products", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


    @PostMapping(
            value = "/import-excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<?> importProducts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Please upload a valid Excel file", HttpStatus.BAD_REQUEST));
        }

       String filename = file.getOriginalFilename();
        if (filename == null || !(filename.endsWith(".xls") || filename.endsWith(".xlsx"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Please upload a valid Excel file", HttpStatus.BAD_REQUEST));
        }

        try {
            productService.importProductsExcel(file);
            return ResponseEntity.ok(new ApiResponse<>("Products imported successfully", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to import products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping(value = "/import-word", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importProductsFromWord(@RequestParam("file") MultipartFile file) {
        try {
            productService.importProductsFromWord(file);
            return ResponseEntity.ok(new ApiResponse<>("Products imported successfully", HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to import products: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


}
