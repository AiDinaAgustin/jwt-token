package com.example.jwt_token.controller;

import com.example.jwt_token.dto.TrxTestAnswerRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.TrxTestAnswerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trx-test-answers")
public class TrxTestAnswerController {
    private final TrxTestAnswerService trxTestAnswerService;

    // Get All
    @GetMapping
    public ResponseEntity<?> getAll() {
        var answers = trxTestAnswerService.getAll();
        return ResponseEntity.ok(new ApiResponse<>("Test Answers retrieved successfully", HttpStatus.OK, answers));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var answer = trxTestAnswerService.getById(id);
        if (answer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Test Answer not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Test Answer retrieved successfully", HttpStatus.OK, answer));
    }

    // Create
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TrxTestAnswerRequest request) {
        try {
            var createdAnswer = trxTestAnswerService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Test Answer created successfully", HttpStatus.CREATED, createdAnswer));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody TrxTestAnswerRequest request) {
        var existingAnswer = trxTestAnswerService.update(id, request);
        if (existingAnswer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Test Answer not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Test Answer updated successfully", HttpStatus.OK, existingAnswer));
    }
}
