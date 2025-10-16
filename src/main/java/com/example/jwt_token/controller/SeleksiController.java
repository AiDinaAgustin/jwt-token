package com.example.jwt_token.controller;

import com.example.jwt_token.dto.SeleksiRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.service.SeleksiService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/seleksi")
public class SeleksiController {

    private final SeleksiService seleksiService;

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
}
