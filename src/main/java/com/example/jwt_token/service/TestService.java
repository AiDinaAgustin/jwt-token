package com.example.jwt_token.service;

import com.example.jwt_token.dto.TestRequest;
import com.example.jwt_token.model.Test;
import com.example.jwt_token.repository.TestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    // Get All Test
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    // Get Test by ID
    public Test getTestById(Long id) {
        return testRepository.findById(id).orElse(null);
    }

    // Create Test
    public Test createTest(TestRequest testRequest) {
        Test test = new Test();
        test.setName(testRequest.getName());
        test.setDurasi(testRequest.getDurasi());
        test.setJumlah_soal(testRequest.getJumlah_soal());
        test.setKeterangan(testRequest.getKeterangan());
        test.setIsrandomquestion(false);
        test.setCreatedAt(Instant.now().toEpochMilli());
        test.setUpdatedAt(Instant.now().toEpochMilli());
        return testRepository.save(test);
    }

    // Update Test
    public Test updateTest(Long id, TestRequest testRequest) {
        Test existingTest = testRepository.findById(id).orElse(null);
        if (existingTest != null) {
            existingTest.setName(testRequest.getName());
            existingTest.setDurasi(testRequest.getDurasi());
            existingTest.setJumlah_soal(testRequest.getJumlah_soal());
            existingTest.setKeterangan(testRequest.getKeterangan());
            existingTest.setUpdatedAt(Instant.now().toEpochMilli());
            return testRepository.save(existingTest);
        }

        return null;
    }

    // Delete Test
    public boolean deleteTest(Long id) {
        if (testRepository.existsById(id)) {
            testRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
