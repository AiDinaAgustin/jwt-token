package com.example.jwt_token.controller;

import com.example.jwt_token.dto.SekolahRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.SekolahService;
import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/sekolah")
public class SekolahController {

    private final SekolahService sekolahService;

    // Get All Sekolah
    @GetMapping
    public ResponseEntity<?> getAllSekolah() {
        var sekolah = sekolahService.getAllSekolah();
        return ResponseEntity.ok(new ApiResponse<>("Sekolah retrieved successfully", HttpStatus.OK, sekolah));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSekolahById(@PathVariable Long id) {
        var sekolah = sekolahService.getSekolahById(id);
        if (sekolah == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Sekolah not found", HttpStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(new ApiResponse<>("Sekolah retrieved successfully", HttpStatus.OK, sekolah));
    }

    // Create Sekolah
    @PostMapping
    public ResponseEntity<?> createSekolah(@Valid @RequestBody SekolahRequest sekolahRequest) {
        var createdSekolah = sekolahService.createSekolah(sekolahRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Sekolah created successfully", HttpStatus.CREATED, createdSekolah));
    }

    // Update Sekolah
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSekolah(@PathVariable Long id, @Valid @RequestBody SekolahRequest sekolahRequest) {
        var existingSekolah = sekolahService.getSekolahById(id);
        if (existingSekolah == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Sekolah not found", HttpStatus.NOT_FOUND));
        }

        return ResponseEntity.ok(new ApiResponse<>("Sekolah updated successfully", HttpStatus.OK, sekolahService.updateSekolah(id, sekolahRequest)));
    }

    // Delete Sekolah
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSekolah(@PathVariable Long id) {
        boolean isDeleted = sekolahService.deleteSekolah(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Sekolah not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Sekolah deleted successfully", HttpStatus.OK));
    }
}
