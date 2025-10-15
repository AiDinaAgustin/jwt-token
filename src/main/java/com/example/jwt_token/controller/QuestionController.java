package com.example.jwt_token.controller;


import com.example.jwt_token.dto.QuestionRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.QuestionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    // Get All
    @GetMapping
    public ResponseEntity<? > getAllQuestions() {
        var questions = questionService.getAllQuestions();
        return ResponseEntity.ok(new ApiResponse<>("Questions retrieved successfully", HttpStatus.OK, questions));
    }

    // Create Question
    @PostMapping
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequest questionRequest) {
        try {
            var createdQuestion = questionService.createQuestion(questionRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Question created successfully", HttpStatus.CREATED, createdQuestion));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST, null));
        }
    }

    // Update Question
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest questionRequest) {
        try {
            var updatedQuestion = questionService.updateQuestion(id, questionRequest);
            if (updatedQuestion != null) {
                return ResponseEntity.ok(new ApiResponse<>("Question updated successfully", HttpStatus.OK, updatedQuestion));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Question not found", HttpStatus.NOT_FOUND, null));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST, null));
        }
    }

    // Delete Question
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        boolean isDeleted = questionService.deleteQuestion(id);
        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse<>("Question deleted successfully", HttpStatus.OK, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Question not found", HttpStatus.NOT_FOUND, null));
        }
    }
}
