package com.example.jwt_token.service;

import com.example.jwt_token.model.Transaction;
import com.example.jwt_token.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
}
