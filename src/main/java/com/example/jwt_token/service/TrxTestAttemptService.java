package com.example.jwt_token.service;

import com.example.jwt_token.dto.TrxTestAttemptRequest;
import com.example.jwt_token.model.Peserta;
import com.example.jwt_token.model.StatusTest;
import com.example.jwt_token.model.Test;
import com.example.jwt_token.model.TrxTestAttempt;
import com.example.jwt_token.repository.TrxTestAttemptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class TrxTestAttemptService {
    private final TrxTestAttemptRepository trxTestAttemptRepository;
    private final PesertaService pesertaService;
    private final TestService testService;

    // Get All
    public List<TrxTestAttempt> getAll() {
        return trxTestAttemptRepository.findAll();
    }

    // Get by ID
    public TrxTestAttempt getById(Long id) {
        return trxTestAttemptRepository.findById(id).orElse(null);
    }

    // Create TrxTestAttempt
    public TrxTestAttempt create(TrxTestAttemptRequest request) {
        Peserta peserta = pesertaService.getPesertaById(request.getPesertaId());
        if (peserta == null) {
            throw new IllegalArgumentException("Peserta not found");
        }

        Test test = testService.getTestById(request.getTestId());
        if (test == null) {
            throw new IllegalArgumentException("Test not found");
        }

        // 🔹 Formatter untuk format "DD-MM-YYYY HH:mm"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        LocalDateTime startDateTime = LocalDateTime.parse(request.getStartedAt(), formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(request.getFinishedAt(), formatter);

        long startTimestamp = startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTimestamp = endDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        TrxTestAttempt trxTestAttempt = new TrxTestAttempt();
        trxTestAttempt.setPeserta(peserta);
        trxTestAttempt.setTest(test);
        trxTestAttempt.setScore(request.getScore());
        trxTestAttempt.setStartedAt(startTimestamp);
        trxTestAttempt.setFinishedAt(endTimestamp);
        trxTestAttempt.setStatus(StatusTest.IN_PROGRESS);
        return trxTestAttemptRepository.save(trxTestAttempt);
    }

}
