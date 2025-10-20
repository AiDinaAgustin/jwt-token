package com.example.jwt_token.repository;

import com.example.jwt_token.model.Test;
import com.example.jwt_token.model.TrxTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrxTestAttemptRepository extends JpaRepository<TrxTestAttempt, Long> {
    List<TrxTestAttempt> findAllByTest(Test test);
}
