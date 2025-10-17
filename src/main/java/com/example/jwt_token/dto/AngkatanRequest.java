package com.example.jwt_token.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AngkatanRequest {

    @NotBlank(message = "Nama is required")
    @Size(min = 2, max = 100, message = "Nama must be between 2 and 100 characters")
    private String nama;

    @NotNull(message = "Tahun is required")
    private Long tahun;

    @NotNull(message = "Sekolah ID is required")
    private Long sekolahId;
}
