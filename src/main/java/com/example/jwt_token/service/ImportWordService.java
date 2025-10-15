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

            QuestionSubtest currentSubtest = null;
            Question currentQuestion = null;

            for (XWPFParagraph p : paragraphs) {
                String text = p.getText().trim();

                if (text.isEmpty()) continue;

                // 📘 Subtest
                if (text.startsWith("Subtest:")) {
                    currentSubtest = new QuestionSubtest();
                    currentSubtest.setNama(text.replace("Subtest:", "").trim());
                    currentSubtest.setDeskripsi("Imported from Word");
                    currentSubtest.setIsbagian(false);
                    currentSubtest.setTest(test);
                    currentSubtest.setCreatedAt(System.currentTimeMillis());
                    subtestRepository.save(currentSubtest);
                }

                // 🧩 Question
                else if (text.startsWith("Q:")) {
                    currentQuestion = new Question();
                    currentQuestion.setPertanyaan(text.replace("Q:", "").trim());
                    currentQuestion.setRingkasan("-");
                    currentQuestion.setJenis("pilihan_ganda");
                    currentQuestion.setIsrandomanswer(false);
                    currentQuestion.setSub_jenis_test("umum");
                    currentQuestion.setQuestionSubtest(currentSubtest);
                    currentQuestion.setCreatedAt(System.currentTimeMillis());
                    questionRepository.save(currentQuestion);
                }

                // ✅ Answer (teks diawali A:, B:, C:, D:)
                else if (text.matches("^[A-Da-d]\\..*")) {
                    boolean isCorrect = text.contains("*"); // tanda * untuk jawaban benar
                    String answerText = text.replace("*", "").trim();

                    Answer answer = new Answer();
                    answer.setTeks(answerText);
                    answer.setBobot(isCorrect ? 1L : 0L);
                    answer.setIsanswer(isCorrect);
                    answer.setQuestion(currentQuestion);
                    answer.setCreatedAt(System.currentTimeMillis());
                    answerRepository.save(answer);
                }
            }
        }
    }
}
