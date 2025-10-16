package com.example.jwt_token.dto;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Durasi is required")
    private Long durasi;

    @NotNull(message = "Jumlah soal is required")
    private Long jumlah_soal;

    @NotNull(message = "Keterangan is required")
    private String keterangan;

    private Boolean israndomquestion;
}
