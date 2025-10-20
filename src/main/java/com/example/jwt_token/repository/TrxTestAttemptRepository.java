package com.example.jwt_token.repository;

import com.example.jwt_token.model.TrxTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrxTestAttemptRepository extends JpaRepository<TrxTestAttempt, Long> {
}
