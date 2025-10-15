package com.example.jwt_token.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "mst_tests")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long durasi;

    @Column(nullable = false)
    private Long jumlah_soal;

    private Boolean israndomquestion;

    @Column(nullable = false)
    private String keterangan;

    private Long createdAt;
    private Long updatedAt;
    private Long deteletedAt;

    @OneToMany(mappedBy = "test",
            fetch = FetchType.LAZY,
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"test"})
    private List<QuestionSubtest> mst_question_subtests;
}
