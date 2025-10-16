package com.example.jwt_token.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TestImportRequest {
    private String name;
    private Long durasi;
    private Long jumlah_soal;
    private String keterangan;
    private boolean isRandomAnswer;
    private MultipartFile file;
}
