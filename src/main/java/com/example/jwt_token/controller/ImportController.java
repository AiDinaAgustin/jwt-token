package com.example.jwt_token.controller;

import com.example.jwt_token.service.ImportWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class ImportController {

    private final ImportWordService importWordService;

    @PostMapping(value = "/{testId}/import-word",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> importWord(
            @PathVariable Long testId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            importWordService.importFromWord(file, testId);
            return ResponseEntity.ok("Import berhasil untuk Test ID: " + testId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Gagal import: " + e.getMessage());
        }
    }
}
