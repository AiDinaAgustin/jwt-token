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

            QuestionSubtest currentParentSubtest = null;
            QuestionSubtest currentBagian = null;
            Question currentQuestion = null;
            String currentJenis = "PILIHAN GANDA"; // default

            // Gabungkan pembacaan dari paragraph dan table
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

                // QUESTION pakai pola nomor (misal: "1. ..." atau "23. ...")
                else if (text.matches("^\\d+\\..*")) {
                    if (currentBagian == null) {
                        throw new RuntimeException("Pertanyaan ditemukan sebelum Bagian Soal!");
                    }

                    currentQuestion = new Question();
                    currentQuestion.setPertanyaan(text.replaceFirst("^\\d+\\.", "").trim());
                    currentQuestion.setRingkasan("-");
                    currentQuestion.setJenis(currentJenis); // ambil jenis dari subtest parent
                    if (currentQuestion.getJenis().equalsIgnoreCase("PILIHAN GANDA")) {
                        currentQuestion.setIsrandomanswer(isRandomAnswer);
                    } else {
                        currentQuestion.setIsrandomanswer(false);
                    }
                    currentQuestion.setSub_jenis_test(currentBagian.getParent().getNama());
                    currentQuestion.setQuestionSubtest(currentBagian);
                    currentQuestion.setCreatedAt(System.currentTimeMillis());
                    questionRepository.save(currentQuestion);
                }

                // ANSWER (A-D)
                else if (text.matches("^[A-Da-d]\\..*")) {
                    if (currentQuestion == null) {
                        throw new RuntimeException("Jawaban ditemukan sebelum Pertanyaan!");
                    }

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
