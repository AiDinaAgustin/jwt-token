package com.example.jwt_token.service;

import com.example.jwt_token.dto.SeleksiRequest;
import com.example.jwt_token.dto.TrxSeleksiTestRequest;
import com.example.jwt_token.model.*;
import com.example.jwt_token.repository.*;
import lombok.AllArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;
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
public class SeleksiService {

    private final String UPLOAD_DIR = "uploads/questions/";

    private final SeleksiRepository seleksiRepository;
    private final SekolahService sekolahService;
    private final AngkatanService angkatanService;
    private final TrxSeleksiTestRepository trxSeleksiTestRepository;
    private final TestService testService;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final QuestionSubtestRepository subtestRepository;
    private final AnswerRepository answerRepository;

    // Get All Seleksi
    public List<Seleksi> getAllSeleksi() {
        return seleksiRepository.findAll();
    }

    // Get Seleksi by ID
    public Seleksi getSeleksiById(Long id) {
        return seleksiRepository.findById(id).orElse(null);
    }

    // Apply test by seleksi
    public TrxSeleksiTests applyTestBySeleksi(Long id, TrxSeleksiTestRequest trxSeleksiTestRequest) {
        Seleksi seleksi = getSeleksiById(id);
        if (seleksi == null) {
            throw new IllegalArgumentException("Seleksi dengan ID " + id + " tidak ditemukan.");
        }

        Test test = testService.getTestById(trxSeleksiTestRequest.getTestid());
        if (test == null) {
            throw new IllegalArgumentException("Test dengan ID " + trxSeleksiTestRequest.getTestid() + " tidak ditemukan.");
        }

        TrxSeleksiTests trxSeleksiTests = new TrxSeleksiTests();
        trxSeleksiTests.setSeleksi(seleksi);
        trxSeleksiTests.setTest(test);
        trxSeleksiTests.setCreatedAt(Instant.now().toEpochMilli());
        return trxSeleksiTestRepository.save(trxSeleksiTests);
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

    // Create Test
    public Map<String, Object> importFromWord(MultipartFile file, Long seleksiId, Long testId, boolean isRandomAnswer) throws Exception {

        Seleksi seleksi = seleksiRepository.findById(seleksiId).orElse(null);
        if (seleksi == null) {
            throw new RuntimeException("Seleksi ID tidak ditemukan");
        }

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

        // pastikan folder upload ada
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        try (InputStream is = file.getInputStream()) {
            XWPFDocument document = new XWPFDocument(is);

            QuestionSubtest currentParentSubtest = null;
            QuestionSubtest currentBagian = null;
            Question currentQuestion = null;
            String currentJenis = "PILIHAN GANDA";

            // Loop semua elemen di dokumen, termasuk paragraf dan gambar
            for (IBodyElement element : document.getBodyElements()) {

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

                    // Deteksi SUB_TEST
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

                    // Deteksi BAGIAN_SOAL
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

                    // Deteksi nomor soal (misal "1. ..." atau "2. ...")
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

                        if (currentBagian.getQuestions() == null) {
                            currentBagian.setQuestions(new ArrayList<>());
                        }
                        currentBagian.getQuestions().add(currentQuestion);
                        continue;
                    }

                    // Deteksi jawaban (A-D)
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

                        if (currentQuestion.getAnswers() == null) {
                            currentQuestion.setAnswers(new ArrayList<>());
                        }
                        currentQuestion.getAnswers().add(answer);
                    }
                }

                // Jika elemen adalah tabel
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
                "data", Map.of(
                        "subtests", importedSubtests
                )
        );
    }

    public Test updateTest(Test test) {
        test.setUpdatedAt(System.currentTimeMillis());
        return testRepository.save(test);
    }
}
