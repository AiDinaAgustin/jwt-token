package com.example.jwt_token.controller;

import com.example.jwt_token.dto.PesertaRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.PesertaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/peserta")
public class PesertaController {
    private final PesertaService pesertaService;

    // Get All
    @GetMapping
    public ResponseEntity<?> getAllPeserta() {
        var peserta = pesertaService.getAllPeserta();
        return ResponseEntity.ok(new ApiResponse<>("Peserta retrieved successfully", HttpStatus.OK, peserta));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPesertaById(@PathVariable Long id) {
        var peserta = pesertaService.getPesertaById(id);
        if (peserta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Peserta not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Peserta retrieved successfully", HttpStatus.OK, peserta));
    }

    // Create Peserta
    @PostMapping
    public ResponseEntity<?> createPeserta(@Valid @RequestBody PesertaRequest pesertaRequest) {
        var createdPeserta = pesertaService.createPeserta(pesertaRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Peserta created successfully", HttpStatus.CREATED, createdPeserta));
    }

    // Update Peserta
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePeserta(@PathVariable Long id, @Valid @RequestBody PesertaRequest pesertaRequest) {
        var existingPeserta = pesertaService.updatePeserta(id, pesertaRequest);
        if (existingPeserta == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Peserta not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Peserta updated successfully", HttpStatus.OK, existingPeserta));
    }

    // Delete Peserta
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePeserta(@PathVariable Long id) {
        boolean isDeleted = pesertaService.deletePeserta(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Peserta not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Peserta deleted successfully", HttpStatus.OK));
    }
}
