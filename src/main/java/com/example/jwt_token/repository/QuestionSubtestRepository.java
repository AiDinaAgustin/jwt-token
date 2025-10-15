package com.example.jwt_token.repository;

import com.example.jwt_token.model.QuestionSubtest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionSubtestRepository extends JpaRepository<QuestionSubtest, Long> {
    @Query("SELECT q FROM QuestionSubtest q WHERE LOWER(q.nama) = LOWER(:name) AND q.test.id = :testId")
    QuestionSubtest findByNamaAndTestId(@Param("name") String name, @Param("testId") Long testId);}
