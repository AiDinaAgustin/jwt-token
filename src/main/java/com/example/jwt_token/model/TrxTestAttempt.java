package com.example.jwt_token.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trx_test_attempts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TrxTestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JoinColumn(name = "peserta_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("testAttempts")
    private Peserta peserta;

    @JoinColumn(name = "test_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("mst_question_subtests")
    private Test test;

    @Column(nullable = false)
    private Long startedAt;

    @Column(nullable = false
    )
    private Long finishedAt;

    private Double score;

    @Enumerated(EnumType.STRING)
    private StatusTest status;

    private Long createdAt;
    private Long updatedAt;
}
