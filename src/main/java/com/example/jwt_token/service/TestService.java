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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final QuestionSubtestRepository subtestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private static final String UPLOAD_DIR = "uploads/questions/";

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

    @Transactional
    public Map<String, Object> importFromWord(MultipartFile file, Long testId, boolean isRandomAnswer) throws Exception {
        Optional<Test> testOpt = testRepository.findById(testId);
        if (testOpt.isEmpty()) {
            throw new RuntimeException("Test ID tidak ditemukan");
        }

        Test test = testOpt.get();

        List<QuestionSubtest> importedSubtests = new ArrayList<>();
        List<Question> importedQuestions = new ArrayList<>();
        List<Answer> importedAnswers = new ArrayList<>();

        int subtestCount = 0;
        int questionCount = 0;
        int answerCount = 0;

        // pastikan folder upload tersedia
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        try (InputStream is = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(is);

            QuestionSubtest currentParentSubtest = null;
            QuestionSubtest currentBagian = null;
            Question currentQuestion = null;
            String currentJenis = "PILIHAN GANDA";

            for (IBodyElement element : document.getBodyElements()) {

                // ================= PARAGRAF =================
                if (element instanceof XWPFParagraph paragraph) {
                    String text = paragraph.getText().trim();

                    // Deteksi apakah paragraf berisi gambar tanpa teks
                    boolean hasPicture = false;
                    for (XWPFRun run : paragraph.getRuns()) {
                        if (!run.getEmbeddedPictures().isEmpty()) {
                            hasPicture = true;
                            break;
                        }
                    }

                    // Jika paragraf hanya berisi gambar → kaitkan dengan soal terakhir
                    if ((text.isEmpty() || text.isBlank()) && hasPicture && currentQuestion != null) {
                        for (XWPFRun run : paragraph.getRuns()) {
                            for (XWPFPicture pic : run.getEmbeddedPictures()) {
                                String imageFileName = UUID.randomUUID() + ".png";
                                Path imagePath = Paths.get(UPLOAD_DIR, imageFileName);

                                try (FileOutputStream fos = new FileOutputStream(imagePath.toFile())) {
                                    fos.write(pic.getPictureData().getData());
                                }

                                currentQuestion.setImg_url("/" + imagePath.toString().replace("\\", "/"));
                                questionRepository.save(currentQuestion); // update DB
                            }
                        }
                        continue;
                    }

                    // SUB_TEST:
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
                        importedSubtests.add(currentParentSubtest);
                        subtestCount++;
                        continue;
                    }

                    // BAGIAN_SOAL:
                    if (text.startsWith("BAGIAN_SOAL:")) {
                        currentBagian = new QuestionSubtest();
                        currentBagian.setNama(text.replace("BAGIAN_SOAL:", "").trim());
                        currentBagian.setDeskripsi("Imported from Word");
                        currentBagian.setIsbagian(true);
                        currentBagian.setTest(test);
                        currentBagian.setParent(currentParentSubtest);
                        currentBagian.setCreatedAt(System.currentTimeMillis());
                        subtestRepository.save(currentBagian);
                        importedSubtests.add(currentBagian);
                        continue;
                    }

                    // Nomor soal
                    if (text.matches("^\\d+\\..*")) {
                        currentQuestion = new Question();
                        currentQuestion.setPertanyaan(text.replaceFirst("^\\d+\\.", "").trim());
                        currentQuestion.setRingkasan("-");
                        currentQuestion.setJenis(currentJenis);
                        currentQuestion.setIsrandomanswer(currentJenis.equalsIgnoreCase("PILIHAN GANDA") && isRandomAnswer);
                        currentQuestion.setSub_jenis_test(currentParentSubtest.getNama());
                        currentQuestion.setQuestionSubtest(currentBagian);
                        currentQuestion.setCreatedAt(System.currentTimeMillis());

                        // Cek apakah ada gambar di paragraf ini
                        for (XWPFRun run : paragraph.getRuns()) {
                            for (XWPFPicture pic : run.getEmbeddedPictures()) {
                                String imageFileName = UUID.randomUUID() + ".png";
                                Path imagePath = Paths.get(UPLOAD_DIR, imageFileName);
                                try (FileOutputStream fos = new FileOutputStream(imagePath.toFile())) {
                                    fos.write(pic.getPictureData().getData());
                                }
                                currentQuestion.setImg_url("/" + imagePath.toString().replace("\\", "/"));
                            }
                        }

                        questionRepository.save(currentQuestion);
                        importedQuestions.add(currentQuestion);
                        questionCount++;

                        if (currentBagian != null) {
                            if (currentBagian.getQuestions() == null)
                                currentBagian.setQuestions(new ArrayList<>());
                            currentBagian.getQuestions().add(currentQuestion);
                        }
                        continue;
                    }

                    // Jawaban
                    if (text.matches("^[A-Da-d]\\..*")) {
                        boolean isCorrect = text.contains("*");
                        String answerText = text.replace("*", "").trim();

                        Answer answer = new Answer();
                        answer.setTeks(answerText);
                        answer.setBobot(1L);
                        answer.setIsanswer(isCorrect);
                        answer.setQuestion(currentQuestion);
                        answer.setCreatedAt(System.currentTimeMillis());

                        answerRepository.save(answer);
                        importedAnswers.add(answer);
                        answerCount++;

                        if (currentQuestion.getAnswers() == null)
                            currentQuestion.setAnswers(new ArrayList<>());
                        currentQuestion.getAnswers().add(answer);
                    }
                }

                // ================= TABEL =================
                if (element instanceof XWPFTable table) {
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            for (XWPFParagraph p : cell.getParagraphs()) {
                                String text = p.getText().trim();
                                if (text.matches("^\\d+\\..*")) {
                                    currentQuestion = new Question();
                                    currentQuestion.setPertanyaan(text.replaceFirst("^\\d+\\.", "").trim());
                                    currentQuestion.setJenis(currentJenis);
                                    currentQuestion.setQuestionSubtest(currentBagian);
                                    currentQuestion.setCreatedAt(System.currentTimeMillis());

                                    // gambar di tabel
                                    for (XWPFRun run : p.getRuns()) {
                                        for (XWPFPicture pic : run.getEmbeddedPictures()) {
                                            String imageFileName = UUID.randomUUID() + ".png";
                                            Path imagePath = Paths.get(UPLOAD_DIR, imageFileName);
                                            try (FileOutputStream fos = new FileOutputStream(imagePath.toFile())) {
                                                fos.write(pic.getPictureData().getData());
                                            }
                                            currentQuestion.setImg_url("/" + imagePath.toString().replace("\\", "/"));
                                        }
                                    }

                                    questionRepository.save(currentQuestion);
                                    importedQuestions.add(currentQuestion);
                                    questionCount++;
                                }
                            }
                        }
                    }
                }
            }
        }

        return Map.of(
                "subtests_imported", subtestCount,
                "questions_imported", questionCount,
                "answers_imported", answerCount,
                "data", Map.of("subtests", importedSubtests)
        );
    }

    public Test updateTest(Test test) {
        test.setUpdatedAt(System.currentTimeMillis());
        return testRepository.save(test);
    }
}
