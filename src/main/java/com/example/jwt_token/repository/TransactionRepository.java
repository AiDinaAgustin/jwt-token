package com.example.jwt_token.repository;

import com.example.jwt_token.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
