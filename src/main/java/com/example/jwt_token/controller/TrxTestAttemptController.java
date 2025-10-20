package com.example.jwt_token.controller;

import com.example.jwt_token.dto.TrxTestAttemptRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.TrxTestAttemptService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/trx-test-attempts")
public class TrxTestAttemptController {
    private final TrxTestAttemptService trxTestAttemptService;

    // Get All
    @GetMapping
    public ResponseEntity<?> getAllTrxTestAttempts() {
        var attempts = trxTestAttemptService.getAll();
        return ResponseEntity.ok(new ApiResponse<>("Test Attempts retrieved successfully", HttpStatus.OK, attempts));
    }

    // Get By ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTrxTestAttemptById(@PathVariable Long id) {
        var attempt = trxTestAttemptService.getById(id);
        if (attempt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("TrxTestAttempt not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("TrxTestAttempt retrieved successfully", HttpStatus.OK, attempt));
    }

    // Create
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TrxTestAttemptRequest request) {
        try {
            var createdAttempt = trxTestAttemptService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("TrxTestAttempt created successfully", HttpStatus.CREATED, createdAttempt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Error creating TrxTestAttempt: " + e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

}
