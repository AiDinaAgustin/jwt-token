package com.example.jwt_token.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PesertaRequest {

    @NotBlank(message = "Nama is required")
    @Size(min = 4, max = 100, message = "Nama must be between 4 and 100 characters")
    private String nama;

    @NotBlank(message = "Email is required")
    @Size(min = 4, max = 100, message = "Email must be between 4 and 100 characters")
    @Email
    private String email;

    private String no_peserta;
}
