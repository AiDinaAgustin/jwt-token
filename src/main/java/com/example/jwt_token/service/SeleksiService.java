package com.example.jwt_token.service;

import com.example.jwt_token.dto.SeleksiRequest;
import com.example.jwt_token.model.Angkatan;
import com.example.jwt_token.model.Sekolah;
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
    private final SekolahService sekolahService;
    private final AngkatanService angkatanService;

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
        Sekolah sekolah = sekolahService.getSekolahById(seleksiRequest.getSekolahid());
        if (sekolah == null) {
            throw new IllegalArgumentException("Sekolah dengan ID " + seleksiRequest.getSekolahid() + " tidak ditemukan.");
        }

        Angkatan angkatan = angkatanService.getAngkatanById(seleksiRequest.getAngkatanid());
        if (angkatan == null) {
            throw new IllegalArgumentException("Angkatan dengan ID " + seleksiRequest.getAngkatanid() + " tidak ditemukan.");
        }

        Seleksi seleksi = new Seleksi();
        seleksi.setNama(seleksiRequest.getNama());
        seleksi.setStatus(seleksiRequest.getStatus());
        seleksi.setJenis_peserta(seleksiRequest.getJenis_peserta());
        seleksi.setSekolah(sekolah);
        seleksi.setAngkatan(angkatan);
        seleksi.setTanggal_mulai(seleksiRequest.getTanggal_mulai());
        seleksi.setTanggal_selesai(seleksiRequest.getTanggal_selesai());
        seleksi.setKeterangan(seleksiRequest.getKeterangan());
        seleksi.setCreated_at(Instant.now().toEpochMilli());
        return seleksiRepository.save(seleksi);
    }

    // Update Seleksi
    public Seleksi updateSeleksi(Long id, SeleksiRequest seleksiRequest) {
        return seleksiRepository.findById(id).map(existingSeleksi -> {
            Sekolah sekolah = sekolahService.getSekolahById(seleksiRequest.getSekolahid());
            if (sekolah == null) {
                throw new IllegalArgumentException("Sekolah dengan ID " + seleksiRequest.getSekolahid() + " tidak ditemukan.");
            }

            Angkatan angkatan = angkatanService.getAngkatanById(seleksiRequest.getAngkatanid());
            if (angkatan == null) {
                throw new IllegalArgumentException("Angkatan dengan ID " + seleksiRequest.getAngkatanid() + " tidak ditemukan.");
            }

            existingSeleksi.setNama(seleksiRequest.getNama());
            existingSeleksi.setStatus(seleksiRequest.getStatus());
            existingSeleksi.setJenis_peserta(seleksiRequest.getJenis_peserta());
            existingSeleksi.setSekolah(sekolah);
            existingSeleksi.setAngkatan(angkatan);
            existingSeleksi.setTanggal_mulai(seleksiRequest.getTanggal_mulai());
            existingSeleksi.setTanggal_selesai(seleksiRequest.getTanggal_selesai());
            existingSeleksi.setKeterangan(seleksiRequest.getKeterangan());
            existingSeleksi.setUpdated_at(Instant.now().toEpochMilli());
            return seleksiRepository.save(existingSeleksi);
        }).orElse(null);
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
