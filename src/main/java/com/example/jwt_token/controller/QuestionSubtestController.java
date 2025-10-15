package com.example.jwt_token.controller;

import com.example.jwt_token.dto.QuestionSubtestRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.QuestionSubtestService;
import com.example.jwt_token.service.TestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question-subtests")
@AllArgsConstructor
public class QuestionSubtestController {
    private final QuestionSubtestService questionSubtestService;
    private final TestService testService;

    // Get All QuestionSubtests
    @GetMapping
    public ResponseEntity<?> getAllQuestionSubtests() {
        var questionSubtests = questionSubtestService.getAllQuestionSubtests();
        return ResponseEntity.ok(new ApiResponse<>("Subtest retrieved successfully", HttpStatus.OK, questionSubtests));
    }

    // Create QuestionSubtest
    @PostMapping
    public ResponseEntity<?> createQuestionSubtest(@Valid @RequestBody QuestionSubtestRequest questionSubtestRequest) {
        try {
            var createdSubtest = questionSubtestService.createQuestionSubtest(questionSubtestRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Subtest created successfully", HttpStatus.CREATED, createdSubtest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to create subtest", HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    // Update QuestionSubtest
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestionSubtest(@PathVariable Long id, @Valid @RequestBody QuestionSubtestRequest questionSubtestRequest) {
        try {
            var updatedSubtest = questionSubtestService.updateQuestionSubtest(id, questionSubtestRequest);
            if (updatedSubtest == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Subtest not found", HttpStatus.NOT_FOUND, null));
            }
            return ResponseEntity.ok(new ApiResponse<>("Subtest updated successfully", HttpStatus.OK, updatedSubtest));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update subtest", HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    // Delete QuestionSubtest
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestionSubtest(@PathVariable Long id) {
        boolean isDeleted = questionSubtestService.deleteQuestionSubtest(id);
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse<>("Subtest deleted successfully", HttpStatus.OK, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Subtest not found", HttpStatus.NOT_FOUND, null));
        }
    }
}
