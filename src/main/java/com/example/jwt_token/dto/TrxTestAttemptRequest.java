package com.example.jwt_token.dto;

import com.example.jwt_token.model.Peserta;
import com.example.jwt_token.model.Test;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrxTestAttemptRequest {

    private Long pesertaId;

    private Long testId;

    @NotBlank(message = "startedAt is required")
    private String startedAt;

    @NotBlank(message = "finishedAt is required")
    private String finishedAt;

    private Double score;
}
