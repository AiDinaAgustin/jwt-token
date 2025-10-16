package com.example.jwt_token.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "mst_seleksi")
public class Seleksi {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Size(min = 2, max = 100)
    private String nama;

    @NotNull
    @Column(nullable = false)
    @Size(min = 2, max = 100)
    private String status;

    private Long sekolahid;

    private Long angkatanid;

    @NotNull
    private String jenis_peserta;

    @NotNull
    private LocalDate tanggal_mulai;

    @NotNull
    private LocalDate tanggal_selesai;

    @NotNull
    private String keterangan;


    private Long created_at;
    private Long updated_at;
    private Long deleted_at;
}
