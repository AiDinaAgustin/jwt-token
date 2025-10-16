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

    public void importFromWord(MultipartFile file, Long testId, boolean isRandomAnswer) throws Exception {
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isEmpty()) {
            throw new RuntimeException("Test ID tidak ditemukan");
        }

        Test test = testOpt.get();

        try (InputStream is = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(is);
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            QuestionSubtest currentParentSubtest = null;
            QuestionSubtest currentBagian = null;
            Question currentQuestion = null;
            String currentJenis = "PILIHAN GANDA"; // default

            for (XWPFParagraph p : paragraphs) {
                String text = p.getText().trim();
                if (text.isEmpty()) continue;

                // SUBTEST
                if (text.startsWith("SUB_TEST:")) {
                    currentParentSubtest = new QuestionSubtest();

                    // deteksi jenis soal (contoh: [ESSAY] atau [PILIHAN GANDA])
                    String namaSubtest = text.replace("SUB_TEST:", "").trim();
                    String jenis = "PILIHAN GANDA"; // default

                    if (namaSubtest.contains("[")) {
                        int start = namaSubtest.indexOf("[");
                        int end = namaSubtest.indexOf("]");
                        if (start != -1 && end != -1) {
                            jenis = namaSubtest.substring(start + 1, end).trim().toUpperCase();
                            namaSubtest = namaSubtest.substring(0, start).trim();
                        }
                    }

                    currentJenis = jenis; // simpan untuk semua pertanyaan di subtest ini

                    currentParentSubtest.setNama(namaSubtest);
                    currentParentSubtest.setDeskripsi("Imported from Word");
                    currentParentSubtest.setIsbagian(false);
                    currentParentSubtest.setTest(test);
                    currentParentSubtest.setCreatedAt(System.currentTimeMillis());
                    subtestRepository.save(currentParentSubtest);

                    currentBagian = null;
                    currentQuestion = null;
                }

                // BAGIAN SOAL
                else if (text.startsWith("BAGIAN_SOAL:")) {
                    if (currentParentSubtest == null) {
                        throw new RuntimeException("Bagian Soal ditemukan sebelum Subtest!");
                    }

                    currentBagian = new QuestionSubtest();
                    currentBagian.setNama(text.replace("BAGIAN_SOAL:", "").trim());
                    currentBagian.setDeskripsi("Imported from Word");
                    currentBagian.setIsbagian(true);
                    currentBagian.setTest(test);
                    currentBagian.setParent(currentParentSubtest);
                    currentBagian.setCreatedAt(System.currentTimeMillis());
                    subtestRepository.save(currentBagian);

                    currentQuestion = null;
                }

                // QUESTION
                else if (text.startsWith("Q:")) {
                    if (currentBagian == null) {
                        throw new RuntimeException("Pertanyaan ditemukan sebelum Bagian Soal!");
                    }

                    currentQuestion = new Question();
                    currentQuestion.setPertanyaan(text.replace("Q:", "").trim());
                    currentQuestion.setRingkasan("-");
                    currentQuestion.setJenis(currentJenis); // ambil jenis dari subtest parent
                    if (currentQuestion != null && currentQuestion.getJenis().equals("PILIHAN GANDA")) {
                        currentQuestion.setIsrandomanswer(isRandomAnswer);
                    } else {
                        currentQuestion.setIsrandomanswer(false);
                    }
                    currentQuestion.setSub_jenis_test(currentBagian.getParent().getNama());
                    currentQuestion.setQuestionSubtest(currentBagian);
                    currentQuestion.setCreatedAt(System.currentTimeMillis());
                    questionRepository.save(currentQuestion);
                }

                // ANSWER (hanya berlaku untuk PILIHAN GANDA)
                else if (text.matches("^[A-Da-d]\\..*")) {
                    if (currentQuestion == null) {
                        throw new RuntimeException("Jawaban ditemukan sebelum Pertanyaan!");
                    }

                    // hanya buat jawaban jika jenis = PILIHAN GANDA
                    if (currentJenis.equalsIgnoreCase("PILIHAN GANDA")) {
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
}
