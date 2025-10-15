package com.example.jwt_token.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "mst_question_subtests")
public class QuestionSubtest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String nama;

    @Column(nullable = false)
    private String deskripsi;

    private Boolean isbagian;

    private Long createdAt;
    private Long updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // foreign key mengarah ke id di tabel yang sama
    private QuestionSubtest parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", referencedColumnName = "id")
    private Test test;
}
