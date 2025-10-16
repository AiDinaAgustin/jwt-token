package com.example.jwt_token.controller;


import com.example.jwt_token.dto.TestRequest;
import com.example.jwt_token.model.Test;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.TestService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    @PostMapping(value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Transactional // rollback otomatis jika gagal
    public ResponseEntity<?> createTestAndImport(
            @RequestParam("name") String name,
            @RequestParam("durasi") Long durasi,
            @RequestParam("keterangan") String keterangan,
            @RequestParam("israndomquestion") boolean isRandomQuestion,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isRandomAnswer", defaultValue = "false") boolean isRandomAnswer
    ) {
        try {
            // 1️Buat test baru
            TestRequest testRequest = new TestRequest();
            testRequest.setName(name);
            testRequest.setDurasi(durasi);
            testRequest.setJumlah_soal(0L);
            testRequest.setKeterangan(keterangan);
            testRequest.setIsrandomquestion(isRandomQuestion);

            Test test = testService.createTest(testRequest);

            // Import soal dari file Word
            var result = testService.importFromWord(file, test.getId(), isRandomAnswer);

            // Ambil jumlah soal dari hasil import
            Long jumlahSoal = Long.valueOf(result.get("questions_imported").toString());
            test.setJumlah_soal(jumlahSoal);

            testService.updateTest(test);

            // Response sukses
            return ResponseEntity.ok(Map.of(
                    "message", "Test dan soal berhasil diimport!",
                    "testId", test.getId(),
                    "result", result
            ));

        } catch (Exception e) {
            e.printStackTrace();
            // rollback otomatis karena @Transactional
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Gagal membuat test atau import soal",
                    "detail", e.getMessage()
            ));
        }
    }
}
