package com.example.jwt_token.repository;

import com.example.jwt_token.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.*;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t " +
            "WHERE (:transactionCode IS NULL OR t.transactionCode = :transactionCode)")
    Page<Transaction> search(Long transactionCode, Pageable pageable);
}
