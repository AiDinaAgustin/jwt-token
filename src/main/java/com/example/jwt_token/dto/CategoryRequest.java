package com.example.jwt_token.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 100, message = "Name must be between 4 and 100 characters")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(min = 4, max = 100, message = "Slug must be between 4 and 100 characters")
    private String slug;

    @NotBlank(message = "Description is required")
    private String description;
}
