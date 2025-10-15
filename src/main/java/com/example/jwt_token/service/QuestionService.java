package com.example.jwt_token.service;


import com.example.jwt_token.dto.QuestionRequest;
import com.example.jwt_token.model.Question;
import com.example.jwt_token.model.QuestionSubtest;
import com.example.jwt_token.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionSubtestService questionSubtestService;

    // Get All Questions
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    // Get Question by ID
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    // Create Question
    public Question createQuestion(QuestionRequest questionRequest) {
        QuestionSubtest subtest = questionSubtestService.getQuestionSubtestById(questionRequest.getSubtestId());
        if (subtest == null) {
            throw new IllegalArgumentException("Subtest with ID " + questionRequest.getSubtestId() + " not found.");
        }

        Question question = new Question();
        question.setPertanyaan(questionRequest.getPertanyaan());
        question.setRingkasan(questionRequest.getRingkasan());
        question.setJenis(questionRequest.getJenis());
        question.setIsrandomanswer(questionRequest.getIsrandomanswer());
        question.setSub_jenis_test(questionRequest.getSub_jenis_test());
        question.setCreatedAt(System.currentTimeMillis());
        question.setUpdatedAt(System.currentTimeMillis());
        question.setQuestionSubtest(subtest);

        return questionRepository.save(question);
    }

    // Update Question
    public Question updateQuestion(Long id, QuestionRequest questionRequest) {
        Question existingQuestion = questionRepository.findById(id).orElse(null);
        if (existingQuestion != null) {
            QuestionSubtest subtest = questionSubtestService.getQuestionSubtestById(questionRequest.getSubtestId());
            if (subtest == null) {
                throw new IllegalArgumentException("Subtest with ID " + questionRequest.getSubtestId() + " not found.");
            }
            existingQuestion.setPertanyaan(questionRequest.getPertanyaan());
            existingQuestion.setRingkasan(questionRequest.getRingkasan());
            existingQuestion.setJenis(questionRequest.getJenis());
            existingQuestion.setIsrandomanswer(questionRequest.getIsrandomanswer());
            existingQuestion.setSub_jenis_test(questionRequest.getSub_jenis_test());
            existingQuestion.setUpdatedAt(System.currentTimeMillis());
            existingQuestion.setQuestionSubtest(subtest);
            return questionRepository.save(existingQuestion);
        }
        return null;
    }

    // Delete Question
    public boolean deleteQuestion(Long id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
