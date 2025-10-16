package com.example.jwt_token.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SekolahRequest {

    @NotBlank(message = "Nama is required")
    @Size(min = 4, max = 100, message = "Nama must be between 4 and 100 characters")
    private String nama;

    @NotBlank(message = "Singkatan is required")
    @Size(min = 4, max = 200, message = "Singkatan must be between 4 and 200 characters")
    private String singkatan;
}
