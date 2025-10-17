package com.example.jwt_token.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "trx_seleksi_tests")
public class TrxSeleksiTests {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seleksiid", referencedColumnName = "id")
    private Seleksi seleksi;

    @ManyToOne
    @JoinColumn(name = "testid", referencedColumnName = "id")
    private Test test;

    private Long createdAt;
    private Long updatedAt;
}
