package com.example.jwt_token.repository;

import com.example.jwt_token.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
