package com.example.jwt_token.service;

import com.example.jwt_token.dto.SeleksiRequest;
import com.example.jwt_token.model.Seleksi;
import com.example.jwt_token.repository.SeleksiRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class SeleksiService {

    private final SeleksiRepository seleksiRepository;

    // Get All Seleksi
    public List<Seleksi> getAllSeleksi() {
        return seleksiRepository.findAll();
    }

    // Get Seleksi by ID
    public Seleksi getSeleksiById(Long id) {
        return seleksiRepository.findById(id).orElse(null);
    }


    // Create Seleksi
    public Seleksi createSeleksi(SeleksiRequest seleksiRequest) {
        Seleksi seleksi = new Seleksi();
        seleksi.setNama(seleksiRequest.getNama());
        seleksi.setStatus(seleksiRequest.getStatus());
        seleksi.setJenis_peserta(seleksiRequest.getJenis_peserta());
        seleksi.setSekolahid(seleksiRequest.getSekolahid());
        seleksi.setAngkatanid(seleksiRequest.getAngkatanid());
        seleksi.setTanggal_mulai(seleksiRequest.getTanggal_mulai());
        seleksi.setTanggal_selesai(seleksiRequest.getTanggal_selesai());
        seleksi.setKeterangan(seleksiRequest.getKeterangan());
        seleksi.setCreated_at(Instant.now().toEpochMilli());
        return seleksiRepository.save(seleksi);
    }

    // Update Seleksi
    public Seleksi updateSeleksi(Long id, SeleksiRequest seleksiRequest) {
        Seleksi seleksi = seleksiRepository.findById(id).orElse(null);

        if(seleksi != null) {
            seleksi.setNama(seleksiRequest.getNama());
            seleksi.setStatus(seleksiRequest.getStatus());
            seleksi.setJenis_peserta(seleksiRequest.getJenis_peserta());
            seleksi.setSekolahid(seleksiRequest.getSekolahid());
            seleksi.setAngkatanid(seleksiRequest.getAngkatanid());
            seleksi.setTanggal_mulai(seleksiRequest.getTanggal_mulai());
            seleksi.setTanggal_selesai(seleksiRequest.getTanggal_selesai());
            seleksi.setKeterangan(seleksiRequest.getKeterangan());
            seleksi.setUpdated_at(Instant.now().toEpochMilli());
            return seleksiRepository.save(seleksi);
        }
        return null;
    }

    // Delete Seleksi
    public boolean deleteSeleksi(Long id) {
        Seleksi seleksi = seleksiRepository.findById(id).orElse(null);
        if(seleksi != null) {
            seleksi.setDeleted_at(Instant.now().toEpochMilli());
            seleksiRepository.save(seleksi);
            return true;
        }
        return false;
    }
}
