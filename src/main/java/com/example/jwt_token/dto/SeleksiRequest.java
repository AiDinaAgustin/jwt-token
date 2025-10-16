package com.example.jwt_token.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeleksiRequest {

    @NotBlank
    @Size(min = 2, max = 100, message = "Nama must be between 2 and 100 characters")
    private String nama;

    @NotBlank
    @Size(min = 2, max = 100, message = "Status must be between 2 and 100 characters")
    private String status;

    private Long sekolahid;

    private Long angkatanid;

    @NotBlank
    @Size(min = 2, max = 100, message = "Jenis Peserta must be between 2 and 100 characters")
    private String jenis_peserta;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate tanggal_mulai;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate tanggal_selesai;

    @NotNull
    private String keterangan;
}
