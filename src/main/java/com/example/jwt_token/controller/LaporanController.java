package com.example.jwt_token.controller;

import com.example.jwt_token.service.LaporanService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/laporan")
public class LaporanController {
    private final LaporanService testResultExportService;

    @GetMapping("/test")
    public ResponseEntity<byte[]> exportTestResults() throws IOException {
        byte[] excelData = testResultExportService.exportToExcel();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test_results.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }
}
