package com.example.jwt_token.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mst_sekolah")
public class Sekolah {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false, unique = true)
    private String singkatan;

    private Long created_at;
    private Long updated_at;
    private Long deleted_at;
}
