package com.example.jwt_token.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trx_test_answers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TrxTestAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JoinColumn(name = "attempt_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("answers")
    private TrxTestAttempt trxTestAttempt;

    @JoinColumn(name = "question_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("answers")
    private Question question;

    // Jika jawaban pilihan ganda
    @JoinColumn(name = "answer_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("question")
    private Answer selectedAnswer;

    // Jika jawaban essay
    private String essayAnswer;

    private Boolean isCorrect;

    private Long waktuJawab;

    private Long createdAt;
    private Long updatedAt;
}
