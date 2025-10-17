package com.example.jwt_token.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "mst_angkatan")
public class Angkatan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false)
    private Long tahun;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sekolah_id", referencedColumnName = "id")
    private Sekolah sekolah;

    private Long created_at;
    private Long updated_at;
    private Long deleted_at;
}
