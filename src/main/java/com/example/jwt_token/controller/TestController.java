package com.example.jwt_token.controller;


import com.example.jwt_token.dto.TestRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.TestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    // Get all tests
    @GetMapping
    public ResponseEntity<?> getAllTests() {
        var tests = testService.getAllTests();
        return ResponseEntity.ok(new ApiResponse<>("Tests retrieved successfully", HttpStatus.OK, tests));
    }

    // Get test by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTestById(@PathVariable Long id) {
        var test = testService.getTestById(id);
        if (test == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Test not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Test retrieved successfully", HttpStatus.OK, test));
    }

    // Create a new test
    @PostMapping
    public ResponseEntity<?> createTests(@Valid @RequestBody TestRequest testRequest) {
        var createdTest = testService.createTest(testRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Test created successfully", HttpStatus.CREATED, createdTest));
    }

    // Update tests
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTests(@PathVariable Long id, @Valid @RequestBody TestRequest testRequest) {
        var updatedTest = testService.updateTest(id, testRequest);
        if (updatedTest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Test not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Test updated successfully", HttpStatus.OK, updatedTest));
    }

    // Delete tests
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTests(@PathVariable Long id) {
        boolean isDeleted = testService.deleteTest(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Test not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Test deleted successfully", HttpStatus.OK));
    }
}
