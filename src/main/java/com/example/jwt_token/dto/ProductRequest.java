package com.example.jwt_token.dto;

import com.example.jwt_token.model.Category;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(min = 3, max = 100, message = "Slug must be between 3 and 100 characters")
    private String slug;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @NotNull(message = "Category is required")
    private Long categoryId;
}
