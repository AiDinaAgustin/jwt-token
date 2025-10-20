package com.example.jwt_token.service;

import com.example.jwt_token.dto.TrxTestAnswerRequest;
import com.example.jwt_token.model.Answer;
import com.example.jwt_token.model.Question;
import com.example.jwt_token.model.TrxTestAnswer;
import com.example.jwt_token.model.TrxTestAttempt;
import com.example.jwt_token.repository.TrxTestAnswerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class TrxTestAnswerService {
    private final TrxTestAnswerRepository trxTestAnswerRepository;
    private final TrxTestAttemptService trxTestAttemptService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    // Get All
    public List<TrxTestAnswer> getAll() {
        return trxTestAnswerRepository.findAll();
    }

    // Get ID
    public TrxTestAnswer getById(Long id) {
        return trxTestAnswerRepository.findById(id).orElse(null);
    }

    // Create
    public TrxTestAnswer create(TrxTestAnswerRequest request) {
        TrxTestAttempt trxTestAttempt = trxTestAttemptService.getById(request.getAttemptId());
        if (trxTestAttempt == null) {
            throw  new IllegalArgumentException("Invalid attempt ID");
        }

        Question question = questionService.getQuestionById(request.getQuestionId());
        if (question == null) {
            throw new IllegalArgumentException("Invalid question ID");
        }

        Answer answer = answerService.getAnswerById(request.getAnswerId());
        if (answer == null) {
            throw new IllegalArgumentException("Invalid answer ID");
        }

        TrxTestAnswer trxTestAnswer = new TrxTestAnswer();
        trxTestAnswer.setTrxTestAttempt(trxTestAttempt);
        trxTestAnswer.setQuestion(question);
        trxTestAnswer.setWaktuJawab(Instant.now().toEpochMilli());
        trxTestAnswer.setCreatedAt(Instant.now().toEpochMilli());

        // Jika Pilihan ganda atau essay
        if (request.getAnswerId() != null) {
            // tipe pilihan ganda
            Answer selectedAnswer = answerService.getAnswerById(request.getAnswerId());
            if (selectedAnswer == null) {
                throw new IllegalArgumentException("Invalid answer ID");
            }
            trxTestAnswer.setSelectedAnswer(selectedAnswer);
            trxTestAnswer.setEssayAnswer(null);

            // Tentukan Opsi benar atau salah
            trxTestAnswer.setIsCorrect(
                    selectedAnswer.getIsanswer() != null ? selectedAnswer.getIsanswer() : false
            );
        } else if (request.getEssayAnswer() != null && !request.getEssayAnswer().isEmpty()) {
            // tipe essay
            trxTestAnswer.setSelectedAnswer(null);
            trxTestAnswer.setEssayAnswer(request.getEssayAnswer());
            trxTestAnswer.setIsCorrect(null); // untuk essay, isCorrect bisa diatur nanti setelah penilaian manual
        } else {
            throw new IllegalArgumentException("Either answerId or essayAnswer must be provided");
        }

        return trxTestAnswerRepository.save(trxTestAnswer);
    }

    // Update
    public TrxTestAnswer update(Long id, TrxTestAnswerRequest request) {
        TrxTestAnswer trxTestAnswer = trxTestAnswerRepository.findById(id).orElse(null);
        if (trxTestAnswer != null) {
            Question question = questionService.getQuestionById(request.getQuestionId());
            if (question == null) {
                throw new IllegalArgumentException("Invalid question ID");
            }

            trxTestAnswer.setQuestion(question);
            trxTestAnswer.setWaktuJawab(Instant.now().toEpochMilli());
            trxTestAnswer.setUpdatedAt(Instant.now().toEpochMilli());

            // Jika Pilihan ganda atau essay
            if (request.getAnswerId() != null) {
                // tipe pilihan ganda
                Answer selectedAnswer = answerService.getAnswerById(request.getAnswerId());
                if (selectedAnswer == null) {
                    throw new IllegalArgumentException("Invalid answer ID");
                }
                trxTestAnswer.setSelectedAnswer(selectedAnswer);
                trxTestAnswer.setEssayAnswer(null);

                // Tentukan Opsi benar atau salah
                trxTestAnswer.setIsCorrect(
                        selectedAnswer.getIsanswer() != null ? selectedAnswer.getIsanswer() : false
                );
            } else if (request.getEssayAnswer() != null && !request.getEssayAnswer().isEmpty()) {
                // tipe essay
                trxTestAnswer.setSelectedAnswer(null);
                trxTestAnswer.setEssayAnswer(request.getEssayAnswer());
                trxTestAnswer.setIsCorrect(null); // untuk essay, isCorrect bisa diatur nanti setelah penilaian manual
            } else {
                throw new IllegalArgumentException("Either answerId or essayAnswer must be provided");
            }

            return trxTestAnswerRepository.save(trxTestAnswer);
        }
        return null;
    }
}
