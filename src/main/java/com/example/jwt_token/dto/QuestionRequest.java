package com.example.jwt_token.dto;

import com.example.jwt_token.model.QuestionSubtest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class QuestionRequest {

    @NotBlank(message = "Pertanyaan is required")
    private String pertanyaan;

    @NotBlank(message = "Ringkasan is required")
    private String ringkasan;

    @NotBlank(message = "Jenis is required")
    private String jenis;

    @NotNull(message = "israndomanswer is required")
    private Boolean israndomanswer;


    @NotBlank(message = "sub_jenis_test is required")
    private String sub_jenis_test;

    @NotNull(message = "Subtest is required")
    private Long subtestId;
}
