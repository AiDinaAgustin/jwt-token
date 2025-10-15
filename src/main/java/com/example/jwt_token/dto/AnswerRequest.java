package com.example.jwt_token.dto;

import com.example.jwt_token.model.Question;
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
public class AnswerRequest {

    @NotBlank(message = "Teks is required")
    private String teks;

    @NotNull(message = "Bobot is required")
    private Long bobot;

    @NotNull(message = "isanswer is required")
    private Boolean isanswer;

    private Long questionId;

}
