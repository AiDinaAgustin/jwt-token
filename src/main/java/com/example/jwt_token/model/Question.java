package com.example.jwt_token.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "mst_questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String pertanyaan;

    @Column(nullable = false)
    private String ringkasan;

    @Column(nullable = false)
    private String jenis;

    private Boolean israndomanswer;

    private Long createdAt;
    private Long updatedAt;

    private Long deletedAt;

    @Column(nullable = false)
    private String sub_jenis_test;

    @JsonIgnoreProperties({"mst_questions"})
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subtest_id", referencedColumnName = "id")
    private QuestionSubtest questionSubtest;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("question")
    private List<Answer> answers;
}
