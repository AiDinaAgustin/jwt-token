package com.example.jwt_token.service;

import com.example.jwt_token.model.*;
import com.example.jwt_token.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ImportWordService {

    private final TestRepository testRepository;
    private final QuestionSubtestRepository subtestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public void importFromWord(MultipartFile file, Long testId) throws Exception {
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isEmpty()) {
            throw new RuntimeException("Test ID tidak ditemukan");
        }

        Test test = testOpt.get();

        try (InputStream is = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(is);
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            QuestionSubtest currentSubtest = null; // contoh: "Logika Penalaran"
            QuestionSubtest currentBagian = null; // contoh: "Deret"
            Question currentQuestion = null;

            for (XWPFParagraph p : paragraphs) {
                String text = p.getText().trim();
                if (text.isEmpty()) continue;

                // ========== SUBTEST ==========
                if (text.startsWith("Subtest:")) {
                    String subtestName = text.replace("Subtest:", "").trim();

                    currentSubtest = new QuestionSubtest();
                    currentSubtest.setNama(subtestName);
                    currentSubtest.setDeskripsi("Imported from Word");
                    currentSubtest.setIsbagian(false);
                    currentSubtest.setTest(test);
                    currentSubtest.setCreatedAt(System.currentTimeMillis());
                    subtestRepository.save(currentSubtest);

                    // reset bagian & question
                    currentBagian = null;
                    currentQuestion = null;
                }

                // ========== BAGIAN SOAL ==========
                else if (text.startsWith("Bagian Soal:")) {
                    if (currentSubtest == null)
                        throw new RuntimeException("Bagian Soal ditemukan sebelum Subtest!");

                    String bagianName = text.replace("Bagian Soal:", "").trim();

                    currentBagian = new QuestionSubtest();
                    currentBagian.setNama(bagianName);
                    currentBagian.setDeskripsi("Bagian dari subtest: " + currentSubtest.getNama());
                    currentBagian.setIsbagian(true);
                    currentBagian.setParent(currentSubtest);
                    currentBagian.setTest(test);
                    currentBagian.setCreatedAt(System.currentTimeMillis());
                    subtestRepository.save(currentBagian);

                    currentQuestion = null;
                }

                // ========== QUESTION ==========
                else if (text.startsWith("Q:")) {
                    if (currentBagian == null)
                        throw new RuntimeException("Pertanyaan ditemukan sebelum Bagian Soal!");

                    currentQuestion = new Question();
                    currentQuestion.setPertanyaan(text.replace("Q:", "").trim());
                    currentQuestion.setRingkasan("-");
                    currentQuestion.setJenis("PILIHAN GANDA");
                    currentQuestion.setIsrandomanswer(false);
                    currentQuestion.setSub_jenis_test("umum");
                    currentQuestion.setQuestionSubtest(currentBagian);
                    currentQuestion.setCreatedAt(System.currentTimeMillis());
                    questionRepository.save(currentQuestion);
                }

                // ========== ANSWER (A:, B:, C:, D:) ==========
                else if (text.matches("^[A-Da-d]\\..*")) {
                    if (currentQuestion == null)
                        throw new RuntimeException("Jawaban ditemukan sebelum Pertanyaan!");

                    boolean isCorrect = text.contains("*");
                    String answerText = text.replace("*", "").trim();

                    Answer answer = new Answer();
                    answer.setTeks(answerText);
                    answer.setBobot(1L);
                    answer.setIsanswer(isCorrect);
                    answer.setQuestion(currentQuestion);
                    answer.setCreatedAt(System.currentTimeMillis());
                    answerRepository.save(answer);
                }
            }
        }
    }
}
