package com.example.jwt_token.dto;

import com.example.jwt_token.model.Answer;
import com.example.jwt_token.model.Question;
import com.example.jwt_token.model.TrxTestAttempt;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrxTestAnswerRequest {

    private Long attemptId;

    private Long questionId;

    private Long answerId;

    private String essayAnswer;

    private Boolean isCorrect;
}
