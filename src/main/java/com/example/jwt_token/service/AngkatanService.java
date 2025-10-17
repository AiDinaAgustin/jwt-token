package com.example.jwt_token.service;

import com.example.jwt_token.dto.AngkatanRequest;
import com.example.jwt_token.model.Angkatan;
import com.example.jwt_token.model.Sekolah;
import com.example.jwt_token.repository.AngkatanRepository;
import com.example.jwt_token.repository.SekolahRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class AngkatanService {
    private final AngkatanRepository angkatanRepository;
    private final SekolahRepository sekolahRepository;
    private final SekolahService sekolahService;

    // Get All Angkatan
    public List<Angkatan> getAllAngkatan() {
        return angkatanRepository.findAll();
    }

    // Get Angkatan by ID
    public Angkatan getAngkatanById(Long id) {
        return angkatanRepository.findById(id).orElse(null);
    }

    // Create Angkatan
    public Angkatan createAngkatan(AngkatanRequest angkatanRequest) {
        Sekolah sekolah = sekolahService.getSekolahById(angkatanRequest.getSekolahId());
        if (sekolah == null) {
            throw new IllegalArgumentException("Sekolah not found with ID: " + angkatanRequest.getSekolahId());
        }

        Angkatan angkatan = new Angkatan();
        angkatan.setNama(angkatanRequest.getNama());
        angkatan.setTahun(angkatanRequest.getTahun());
        angkatan.setCreated_at(Instant.now().toEpochMilli());
        angkatan.setSekolah(sekolah);

        return angkatanRepository.save(angkatan);
    }

    // Update Angkatan
    public Angkatan updateAngkatan(Long id, AngkatanRequest angkatanRequest) {
        Angkatan existingAngkatan = angkatanRepository.findById(id).orElse(null);
        if (existingAngkatan == null) {
            return null;
        }
        Sekolah sekolah = sekolahService.getSekolahById(angkatanRequest.getSekolahId());
        if (sekolah == null) {
            throw new IllegalArgumentException("Sekolah not found with ID: " + angkatanRequest.getSekolahId());
        }
        existingAngkatan.setNama(angkatanRequest.getNama());
        existingAngkatan.setTahun(angkatanRequest.getTahun());
        existingAngkatan.setUpdated_at(Instant.now().toEpochMilli());
        existingAngkatan.setSekolah(sekolah);

        return angkatanRepository.save(existingAngkatan);
    }

    // Delete Angkatan
    public boolean deleteAngkatan(Long id) {
        Angkatan existingAngkatan = angkatanRepository.findById(id).orElse(null);
        if (existingAngkatan == null) {
            return false;
        }
        angkatanRepository.delete(existingAngkatan);
        return true;
    }
}
