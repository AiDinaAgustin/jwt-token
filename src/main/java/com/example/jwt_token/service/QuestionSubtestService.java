package com.example.jwt_token.service;


import com.example.jwt_token.dto.QuestionSubtestRequest;
import com.example.jwt_token.model.QuestionSubtest;
import com.example.jwt_token.model.Test;
import com.example.jwt_token.repository.QuestionSubtestRepository;
import com.example.jwt_token.repository.TestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionSubtestService {
    private final QuestionSubtestRepository questionSubtestRepository;
    private final TestRepository testRepository;
    private final TestService testService;

    // Get All QuestionSubtest
    public List<QuestionSubtest> getAllQuestionSubtests() {
        return questionSubtestRepository.findAll();
    }

    // Get QuestionSubtest by ID
    public QuestionSubtest getQuestionSubtestById(Long id) {
        return questionSubtestRepository.findById(id).orElse(null);
    }

    // Create QuestionSubtest
    public QuestionSubtest createQuestionSubtest(QuestionSubtestRequest questionSubtestRequest) {
        Test test = testService.getTestById(questionSubtestRequest.getTestId());
        if (test == null) {
            throw new IllegalArgumentException("Test with ID " + questionSubtestRequest.getTestId() + " not found.");
        }

        QuestionSubtest newQuestionSubtest = new QuestionSubtest();
        newQuestionSubtest.setNama(questionSubtestRequest.getNama());
        newQuestionSubtest.setDeskripsi(questionSubtestRequest.getDeskripsi());
        newQuestionSubtest.setIsbagian(false); // Set default value for isbagian
        newQuestionSubtest.setCreatedAt(System.currentTimeMillis());
        newQuestionSubtest.setUpdatedAt(System.currentTimeMillis());
        newQuestionSubtest.setTest(test);

        // handle parentId opsional
        if (questionSubtestRequest.getParentId() != null) {
            QuestionSubtest parent = questionSubtestRepository.findById(questionSubtestRequest.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Parent subtest with ID " + questionSubtestRequest.getParentId() + " not found."));
            newQuestionSubtest.setParent(parent);
        }

        return questionSubtestRepository.save(newQuestionSubtest);
    }

    // Update QuestionSubtest
    public QuestionSubtest updateQuestionSubtest(Long id, QuestionSubtestRequest questionSubtestRequest) {
        QuestionSubtest existingQuestionSubtest = questionSubtestRepository.findById(id).orElse(null);
        if (existingQuestionSubtest == null) {
            return null; // Subtest not found
        }

        Test test = testService.getTestById(questionSubtestRequest.getTestId());
        if (test == null) {
            throw new IllegalArgumentException("Test with ID " + questionSubtestRequest.getTestId() + " not found.");
        }

        existingQuestionSubtest.setNama(questionSubtestRequest.getNama());
        existingQuestionSubtest.setDeskripsi(questionSubtestRequest.getDeskripsi());
        existingQuestionSubtest.setUpdatedAt(System.currentTimeMillis());
        existingQuestionSubtest.setTest(test);

        // handle parentId opsional
        if (questionSubtestRequest.getParentId() != null) {
            QuestionSubtest parent = questionSubtestRepository.findById(questionSubtestRequest.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Parent subtest with ID " + questionSubtestRequest.getParentId() + " not found."));
            existingQuestionSubtest.setParent(parent);
        } else {
            existingQuestionSubtest.setParent(null); // Clear parent if parentId is null

        }
        return questionSubtestRepository.save(existingQuestionSubtest);
    }

    // Delete QuestionSubtest
    public boolean deleteQuestionSubtest(Long id) {
        if (questionSubtestRepository.existsById(id)) {
            questionSubtestRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
