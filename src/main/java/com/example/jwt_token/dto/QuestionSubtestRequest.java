package com.example.jwt_token.dto;

import com.example.jwt_token.model.QuestionSubtest;
import com.example.jwt_token.model.Test;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubtestRequest {

    @NotBlank(message = "Nama is required")
    private String nama;

    @NotNull(message = "Deskripsi is required")
    private String deskripsi;

    private Long parentId;

    @NotNull(message = "Test is required")
    private Long testId;
}
