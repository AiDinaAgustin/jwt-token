package com.example.jwt_token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponse {
    private Long id;
    private String teks;
    private Long bobot;
    private Boolean isanswer;
    private Long questionId;
}
