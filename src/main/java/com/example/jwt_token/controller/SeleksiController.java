package com.example.jwt_token.controller;

import com.example.jwt_token.dto.SeleksiRequest;
import com.example.jwt_token.dto.TestRequest;
import com.example.jwt_token.dto.TrxSeleksiTestRequest;
import com.example.jwt_token.model.Test;
import com.example.jwt_token.model.TrxSeleksiTests;
import com.example.jwt_token.repository.SeleksiRepository;
import com.example.jwt_token.repository.TrxSeleksiTestRepository;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.SeleksiService;
import com.example.jwt_token.service.TestService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/seleksi")
public class SeleksiController {

    private final SeleksiService seleksiService;
    private final TestService testService;
    private final SeleksiRepository seleksiRepository;
    private final TrxSeleksiTestRepository trxSeleksiTestRepository;

    // Get All
    @GetMapping
    public ResponseEntity<?> getAllSeleksi() {
        var seleksi = seleksiService.getAllSeleksi();
        return ResponseEntity.ok(new ApiResponse<>("Seleksi retrieved successfully", HttpStatus.OK, seleksi));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSeleksiById(@PathVariable Long id) {
        var seleksi = seleksiService.getSeleksiById(id);
        if (seleksi == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Seleksi not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Seleksi retrieved successfully", HttpStatus.OK, seleksi));
    }

    // Create Seleksi
    @PostMapping
    public ResponseEntity<?> createSeleksi(@Valid @RequestBody SeleksiRequest seleksiRequest) {
        var createdSeleksi = seleksiService.createSeleksi(seleksiRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Seleksi created successfully", HttpStatus.CREATED, createdSeleksi));
    }

    // Update Seleksi
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeleksi(@PathVariable Long id,@Valid @RequestBody SeleksiRequest seleksiRequest) {
        var existingSeleksi = seleksiService.updateSeleksi(id, seleksiRequest);
        if (existingSeleksi == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Seleksi not found", HttpStatus.NOT_FOUND));
        }

        var updatedSeleksi = seleksiService.updateSeleksi(id, seleksiRequest);
        return ResponseEntity.ok(new ApiResponse<>("Seleksi updated successfully", HttpStatus.OK, updatedSeleksi));
    }

    // Delete Seleksi
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteSeleksi(@PathVariable Long id) {
        boolean isDeleted = seleksiService.deleteSeleksi(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Seleksi not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Seleksi deleted successfully", HttpStatus.OK));
    }

    // Append Tests to Seleksi
    @PostMapping("/{id}/apply-test")
    public ResponseEntity<?> applyTestBySeleksi(
            @PathVariable Long id,
            @RequestBody TrxSeleksiTestRequest trxSeleksiTestRequest
            ) {
        try {
            var updatedSeleksi = seleksiService.applyTestBySeleksi(id, trxSeleksiTestRequest);
            if (updatedSeleksi == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Seleksi not found", HttpStatus.NOT_FOUND));
            }
            return ResponseEntity.ok(new ApiResponse<>("Tests applied to Seleksi successfully", HttpStatus.OK, updatedSeleksi));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to apply tests to Seleksi", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Create Test
    @PostMapping(value = "/{seleksiId}/create-test",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Transactional // rollback otomatis jika gagal
    public ResponseEntity<?> createTestAndImport(
            @PathVariable Long seleksiId,
            @RequestParam("name") String name,
            @RequestParam("durasi") Long durasi,
            @RequestParam("keterangan") String keterangan,
            @RequestParam("israndomquestion") boolean isRandomQuestion,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isRandomAnswer", defaultValue = "false") boolean isRandomAnswer
    ) {
        try {

            var seleksi = seleksiRepository.findById(seleksiId);
            if (seleksi == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "Seleksi tidak ditemukan"
                        ));
            }

            var existingSeleksi = seleksi.get();
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

            // Tambahkan test ke seleksi
            TrxSeleksiTests trxSeleksiTests = new TrxSeleksiTests();
            trxSeleksiTests.setSeleksi(existingSeleksi);
            trxSeleksiTests.setTest(test);
            trxSeleksiTests.setCreatedAt(Instant
                    .now().toEpochMilli());
            trxSeleksiTestRepository.save(trxSeleksiTests);

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
