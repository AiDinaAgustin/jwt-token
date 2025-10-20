package com.example.jwt_token.service;

import com.example.jwt_token.dto.PesertaRequest;
import com.example.jwt_token.model.Peserta;
import com.example.jwt_token.repository.PesertaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class PesertaService {
    private final PesertaRepository pesertaRepository;

    // Get All Peserta
    public List<Peserta> getAllPeserta() {
        return pesertaRepository.findAll();
    }

    // Get Peserta by ID
    public Peserta getPesertaById(Long id) {
        return pesertaRepository.findById(id).orElse(null);
    }

    // Create Peserta
    public Peserta createPeserta(PesertaRequest pesertaRequest) {
        Peserta peserta = new Peserta();

        peserta.setNama(pesertaRequest.getNama());
        peserta.setEmail(pesertaRequest.getEmail());
        peserta.setNo_peserta(generateNoPeserta());
        peserta.setCreatedAt(Instant.now().toEpochMilli());
        return pesertaRepository.save(peserta);
    }

    // Update Peserta
    public Peserta updatePeserta(Long id, PesertaRequest pesertaRequest) {
        Peserta peserta = pesertaRepository.findById(id).orElse(null);
        if (peserta != null) {
            peserta.setNama(pesertaRequest.getNama());
            peserta.setEmail(pesertaRequest.getEmail());
            peserta.setUpdatedAt(Instant.now().toEpochMilli());
            return pesertaRepository.save(peserta);
        }
        return null;
    }

    // Delete Peserta
    public boolean deletePeserta(Long id) {
        Peserta peserta = pesertaRepository.findById(id).orElse(null);
        if (peserta != null) {
            pesertaRepository.delete(peserta);
            return true;
        }
        return false;
    }

    // Generate No Peserta
    public String generateNoPeserta() {
        long count = pesertaRepository.count() + 1;
        return String.format("PST%05d", count);
    }
}
