package com.example.jwt_token.service;

import com.example.jwt_token.dto.SekolahRequest;
import com.example.jwt_token.model.Sekolah;
import com.example.jwt_token.model.Seleksi;
import com.example.jwt_token.repository.SekolahRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class SekolahService {

    private final SekolahRepository sekolahRepository;

    // Get All Sekolah
    public List<Sekolah> getAllSekolah() {
        return sekolahRepository.findAll();
    }

    // Get Sekolah by ID
    public Sekolah getSekolahById(Long id) {
        return sekolahRepository.findById(id).orElse(null);
    }

    // Create Sekolah
    public Sekolah createSekolah(SekolahRequest sekolahRequest) {
        Sekolah sekolah = new Sekolah();

        sekolah.setNama(sekolahRequest.getNama());
        sekolah.setSingkatan(sekolahRequest.getSingkatan());
        sekolah.setCreated_at(Instant.now().toEpochMilli());

        return sekolahRepository.save(sekolah);
    }

    // Update Sekolah
    public Sekolah updateSekolah(Long id, SekolahRequest sekolahRequest) {
        Sekolah sekolah = sekolahRepository.findById(id).orElse(null);

        if (sekolah != null) {
            sekolah.setNama(sekolahRequest.getNama());
            sekolah.setSingkatan(sekolahRequest.getSingkatan());
            sekolah.setUpdated_at(Instant.now().toEpochMilli());
            return sekolahRepository.save(sekolah);
        }

        return null;
    }

    // Delete Sekolah
    public boolean deleteSekolah(Long id) {
        Sekolah sekolah = sekolahRepository.findById(id).orElse(null);
        if (sekolah != null) {
            sekolah.setDeleted_at(Instant.now().toEpochMilli());
            sekolahRepository.save(sekolah);
            return true;
        }
        return false;
    }
}
