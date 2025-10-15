package com.example.jwt_token.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubtestResponse {

    private Long id;
    private String nama;
    private String deskripsi;
    private Boolean isbagian;
    private Long createdAt;
    private Long updatedAt;
    private Long parentId;
    private Long testId;
    
}
