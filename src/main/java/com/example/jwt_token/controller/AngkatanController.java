package com.example.jwt_token.controller;

import com.example.jwt_token.dto.AngkatanRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.AngkatanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/angkatan")
public class AngkatanController {

    private AngkatanService angkatanService;

    // Get All
    @GetMapping
    public ResponseEntity<?> getAll() {
        var angkatan = angkatanService.getAllAngkatan();
        return ResponseEntity.ok(new ApiResponse<>("Angkatan retrieved successfully", HttpStatus.OK, angkatan));
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        var angkatan = angkatanService.getAngkatanById(id);
        if (angkatan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Angkatan not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Angkatan retrieved successfully", HttpStatus.OK, angkatan));
    }

    // Create Angkatan
    @PostMapping
    public ResponseEntity<?> createAngkatan (@Valid @RequestBody AngkatanRequest angkatanRequest) {
        var createdAngkatan = angkatanService.createAngkatan(angkatanRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Angkatan created successfully", HttpStatus.CREATED, createdAngkatan));
    }

    // Update Angkatan
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAngkatan(@PathVariable Long id, @Valid @RequestBody AngkatanRequest angkatanRequest) {
        var updatedAngkatan = angkatanService.updateAngkatan(id, angkatanRequest);
        if (updatedAngkatan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Angkatan not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Angkatan updated successfully", HttpStatus.OK, updatedAngkatan));
    }

    // Delete Angkatan
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAngkatan(@PathVariable Long id) {
        var angkatan = angkatanService.getAngkatanById(id);
        if (angkatan == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Angkatan not found", HttpStatus.NOT_FOUND));
        }
        angkatanService.deleteAngkatan(id);
        return ResponseEntity.ok(new ApiResponse<>("Angkatan deleted successfully", HttpStatus.OK));
    }
}
