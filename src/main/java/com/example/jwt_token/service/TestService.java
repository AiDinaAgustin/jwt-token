package com.example.jwt_token.service;

import com.example.jwt_token.dto.TestRequest;
import com.example.jwt_token.model.Answer;
import com.example.jwt_token.model.Question;
import com.example.jwt_token.model.QuestionSubtest;
import com.example.jwt_token.model.Test;
import com.example.jwt_token.repository.AnswerRepository;
import com.example.jwt_token.repository.QuestionRepository;
import com.example.jwt_token.repository.QuestionSubtestRepository;
import com.example.jwt_token.repository.TestRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final QuestionSubtestRepository subtestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

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
        test.setIsrandomquestion(testRequest.getIsrandomquestion());
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
            existingTest.setIsrandomquestion(testRequest.getIsrandomquestion());
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

    public Map<String, Object> importFromWord(MultipartFile file, Long testId, boolean isRandomAnswer) throws Exception {
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isEmpty()) {
            throw new RuntimeException("Test ID tidak ditemukan");
        }

        Test test = testOpt.get();

        int subtestCount = 0;
        int questionCount = 0;
        int answerCount = 0;

        try (InputStream is = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(is);

            QuestionSubtest currentParentSubtest = null;
            QuestionSubtest currentBagian = null;
            Question currentQuestion = null;
            String currentJenis = "PILIHAN GANDA";

            List<String> allTexts = new ArrayList<>();
            for (XWPFParagraph p : document.getParagraphs()) {
                String text = p.getText().trim();
                if (!text.isEmpty()) allTexts.add(text);
            }

            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            String text = p.getText().trim();
                            if (!text.isEmpty()) allTexts.add(text);
                        }
                    }
                }
            }

            for (String text : allTexts) {
                if (text.startsWith("SUB_TEST:")) {
                    currentParentSubtest = new QuestionSubtest();

                    String namaSubtest = text.replace("SUB_TEST:", "").trim();
                    String jenis = "PILIHAN GANDA";
                    if (namaSubtest.contains("[")) {
                        int start = namaSubtest.indexOf("[");
                        int end = namaSubtest.indexOf("]");
                        if (start != -1 && end != -1) {
                            jenis = namaSubtest.substring(start + 1, end).trim().toUpperCase();
                            namaSubtest = namaSubtest.substring(0, start).trim();
                        }
                    }

                    currentJenis = jenis;
                    currentParentSubtest.setNama(namaSubtest);
                    currentParentSubtest.setDeskripsi("Imported from Word");
                    currentParentSubtest.setIsbagian(false);
                    currentParentSubtest.setTest(test);
                    currentParentSubtest.setCreatedAt(System.currentTimeMillis());
                    subtestRepository.save(currentParentSubtest);
                    subtestCount++;
                }

                else if (text.startsWith("BAGIAN_SOAL:")) {
                    currentBagian = new QuestionSubtest();
                    currentBagian.setNama(text.replace("BAGIAN_SOAL:", "").trim());
                    currentBagian.setDeskripsi("Imported from Word");
                    currentBagian.setIsbagian(true);
                    currentBagian.setTest(test);
                    currentBagian.setParent(currentParentSubtest);
                    currentBagian.setCreatedAt(System.currentTimeMillis());
                    subtestRepository.save(currentBagian);
                }

                else if (text.matches("^\\d+\\..*")) {
                    currentQuestion = new Question();
                    currentQuestion.setPertanyaan(text.replaceFirst("^\\d+\\.", "").trim());
                    currentQuestion.setRingkasan("-");
                    currentQuestion.setJenis(currentJenis);
                    currentQuestion.setIsrandomanswer(currentJenis.equalsIgnoreCase("PILIHAN GANDA") && isRandomAnswer);
                    currentQuestion.setSub_jenis_test(currentParentSubtest.getNama());
                    currentQuestion.setQuestionSubtest(currentBagian);
                    currentQuestion.setCreatedAt(System.currentTimeMillis());
                    questionRepository.save(currentQuestion);
                    questionCount++;
                }

                else if (text.matches("^[A-Da-d]\\..*")) {
                    boolean isCorrect = text.contains("*");
                    String answerText = text.replace("*", "").trim();

                    Answer answer = new Answer();
                    answer.setTeks(answerText);
                    answer.setBobot(1L);
                    answer.setIsanswer(isCorrect);
                    answer.setQuestion(currentQuestion);
                    answer.setCreatedAt(System.currentTimeMillis());
                    answerRepository.save(answer);
                    answerCount++;
                }
            }

        }

        // return summary log
        return Map.of(
                "subtests_imported", subtestCount,
                "questions_imported", questionCount,
                "answers_imported", answerCount
        );
    }

    public Test updateTest(Test test) {
        test.setUpdatedAt(System.currentTimeMillis());
        return testRepository.save(test);
    }
}
