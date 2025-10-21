package com.example.jwt_token.seeder;

import com.example.jwt_token.model.*;
import com.example.jwt_token.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final SekolahRepository sekolahRepository;
    private final AngkatanRepository angkatanRepository;
    private final PesertaRepository pesertaRepository;
    private final TestRepository testRepository;
    private final QuestionSubtestRepository subtestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SeleksiRepository seleksiRepository;
    private final TrxSeleksiTestRepository trxSeleksiTestsRepository;
    private final TrxTestAttemptRepository trxTestAttemptRepository;
    private final TrxTestAnswerRepository trxTestAnswerRepository;
    private final UserRepository userRepository;

    public DatabaseSeeder(
            SekolahRepository sekolahRepository,
            AngkatanRepository angkatanRepository,
            PesertaRepository pesertaRepository,
            TestRepository testRepository,
            QuestionSubtestRepository subtestRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            SeleksiRepository seleksiRepository,
            TrxSeleksiTestRepository trxSeleksiTestsRepository,
            TrxTestAttemptRepository trxTestAttemptRepository,
            TrxTestAnswerRepository trxTestAnswerRepository,
            UserRepository userRepository

    ) {
        this.sekolahRepository = sekolahRepository;
        this.angkatanRepository = angkatanRepository;
        this.pesertaRepository = pesertaRepository;
        this.testRepository = testRepository;
        this.subtestRepository = subtestRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.seleksiRepository = seleksiRepository;
        this.trxSeleksiTestsRepository = trxSeleksiTestsRepository;
        this.trxTestAttemptRepository = trxTestAttemptRepository;
        this.trxTestAnswerRepository = trxTestAnswerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.count() == 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            User admin = new User(
                    "Admin Sistem",
                    "admin",
                    passwordEncoder.encode("password"), // password terenkripsi
                    Role.ROLE_ADMIN
            );

            User user = new User(
                    "Pengguna Umum",
                    "user",
                    passwordEncoder.encode("password"),
                    Role.ROLE_USER
            );

            userRepository.save(admin);
            userRepository.save(user);

            System.out.println("UserSeeder berhasil dijalankan: 2 user default dibuat");
        }

        // Cek dulu biar tidak dobel
        if (sekolahRepository.count() > 0) {
            System.out.println("Data sudah ada, skip seeding.");
            return;
        }

        long now = System.currentTimeMillis();

        // ===== 1. Sekolah =====
        Sekolah sekolah = new Sekolah();
        sekolah.setId(1L);
        sekolah.setNama("SMK Sineas Kreatif");
        sekolah.setSingkatan("SMKSK");
        sekolah.setCreated_at(now);
        sekolah.setUpdated_at(now);
        sekolahRepository.save(sekolah);

        // ===== 2. Angkatan =====
        Angkatan angkatan = new Angkatan();
        angkatan.setId(1L);
        angkatan.setNama("Angkatan 2025");
        angkatan.setTahun(2025L);
        angkatan.setSekolah(sekolah);
        angkatan.setCreated_at(now);
        angkatan.setUpdated_at(now);
        angkatanRepository.save(angkatan);

        // ===== 3. Peserta (5 Orang) =====
        List<Peserta> pesertaList = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            Peserta p = new Peserta();
            p.setId(i);
            p.setNama("Peserta " + i);
            p.setEmail("peserta" + i + "@example.com");
            p.setNo_peserta("P00" + i);
            p.setCreatedAt(now);
            p.setUpdatedAt(now);
            pesertaList.add(p);
        }
        pesertaRepository.saveAll(pesertaList);

        // ===== 4. Test (2 Test) =====
        List<Test> tests = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            Test t = new Test();
            t.setId(i);
            t.setName(i == 1 ? "Tes Logika Dasar" : "Tes Kepribadian");
            t.setDurasi(60L);
            t.setJumlah_soal(2L);
            t.setIsrandomquestion(false);
            t.setKeterangan("Deskripsi untuk " + t.getName());
            t.setCreatedAt(now);
            t.setUpdatedAt(now);
            tests.add(t);
        }
        testRepository.saveAll(tests);

        // ===== 5. QuestionSubtest (1 per Test) =====
        List<QuestionSubtest> subtests = new ArrayList<>();
        for (Test t : tests) {
            QuestionSubtest s = new QuestionSubtest();
            s.setId(t.getId());
            s.setNama("Subtest untuk " + t.getName());
            s.setDeskripsi("Deskripsi subtest " + t.getName());
            s.setIsbagian(false);
            s.setTest(t);
            s.setCreatedAt(now);
            s.setUpdatedAt(now);
            subtests.add(s);
        }
        subtestRepository.saveAll(subtests);

        // ===== 6. Questions + Answers =====
        long questionId = 1;
        long answerId = 1;
        List<Question> questions = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();

        for (QuestionSubtest subtest : subtests) {
            for (int i = 1; i <= 10; i++) {
                Question q = new Question();
                q.setId(questionId);
                q.setPertanyaan("Pertanyaan " + i + " untuk " + subtest.getTest().getName());
                q.setRingkasan("Ringkasan " + i);
                q.setJenis("MULTIPLE_CHOICE");
                q.setIsrandomanswer(false);
                q.setSub_jenis_test(subtest.getNama());
                q.setQuestionSubtest(subtest);
                q.setCreatedAt(now);
                q.setUpdatedAt(now);
                questions.add(q);

                // 2 jawaban per soal (1 benar, 1 salah)
                Answer a1 = new Answer();
                a1.setId(answerId++);
                a1.setTeks("Jawaban Benar " + i);
                a1.setBobot(1L);
                a1.setIsanswer(true);
                a1.setQuestion(q);
                a1.setCreatedAt(now);
                a1.setUpdatedAt(now);

                Answer a2 = new Answer();
                a2.setId(answerId++);
                a2.setTeks("Jawaban Salah " + i);
                a2.setBobot(1L);
                a2.setIsanswer(false);
                a2.setQuestion(q);
                a2.setCreatedAt(now);
                a2.setUpdatedAt(now);

                answers.add(a1);
                answers.add(a2);

                questionId++;
            }
        }

        questionRepository.saveAll(questions);
        answerRepository.saveAll(answers);

        // ===== 7. Seleksi =====
        Seleksi seleksi = new Seleksi();
        seleksi.setId(1L);
        seleksi.setNama("Seleksi Karyawan 2025");
        seleksi.setStatus("Aktif");
        seleksi.setSekolah(sekolah);
        seleksi.setAngkatan(angkatan);
        seleksi.setJenis_peserta("Umum");
        seleksi.setTanggal_mulai(LocalDate.now());
        seleksi.setTanggal_selesai(LocalDate.now().plusDays(7));
        seleksi.setKeterangan("Seleksi awal untuk karyawan 2025");
        seleksi.setCreated_at(now);
        seleksi.setUpdated_at(now);
        seleksiRepository.save(seleksi);

        // ===== 8. TrxSeleksiTests =====
        List<TrxSeleksiTests> trxSeleksiTests = new ArrayList<>();
        for (Test t : tests) {
            TrxSeleksiTests trx = new TrxSeleksiTests();
            trx.setId(t.getId());
            trx.setSeleksi(seleksi);
            trx.setTest(t);
            trx.setCreatedAt(now);
            trx.setUpdatedAt(now);
            trxSeleksiTests.add(trx);
        }
        trxSeleksiTestsRepository.saveAll(trxSeleksiTests);

        // ===== 9. TrxTestAttempt & TrxTestAnswer =====
        List<TrxTestAttempt> attempts = new ArrayList<>();
        List<TrxTestAnswer> trxAnswers = new ArrayList<>();
        long attemptId = 1;
        long trxAnswerId = 1;

        for (Peserta p : pesertaList) {
            for (Test t : tests) {
                TrxTestAttempt attempt = new TrxTestAttempt();
                attempt.setId(attemptId++);
                attempt.setPeserta(p);
                attempt.setTest(t);
                attempt.setStartedAt(now);
                attempt.setFinishedAt(now + 600000L);
                attempt.setScore(0.0);
                attempt.setStatus(StatusTest.COMPLETED);
                attempt.setCreatedAt(now);
                attempt.setUpdatedAt(now);
                attempts.add(attempt);

                // Jawaban untuk setiap pertanyaan di test ini
                List<Question> qs = questionRepository.findAll()
                        .stream()
                        .filter(q -> q.getQuestionSubtest().getTest().getId().equals(t.getId()))
                        .toList();

                for (Question q : qs) {
                    List<Answer> ansList = answerRepository.findAll()
                            .stream()
                            .filter(a -> a.getQuestion().getId().equals(q.getId()))
                            .toList();

                    TrxTestAnswer trxAns = new TrxTestAnswer();
                    trxAns.setId(trxAnswerId++);
                    trxAns.setTrxTestAttempt(attempt);
                    trxAns.setQuestion(q);
                    trxAns.setSelectedAnswer(ansList.get(0)); // pilih jawaban benar
                    trxAns.setIsCorrect(true);
                    trxAns.setWaktuJawab(10000L);
                    trxAns.setCreatedAt(now);
                    trxAns.setUpdatedAt(now);
                    trxAnswers.add(trxAns);
                }
            }
        }

        trxTestAttemptRepository.saveAll(attempts);
        trxTestAnswerRepository.saveAll(trxAnswers);

        System.out.println("✅ Database seeding selesai! 🎉");
    }
}
