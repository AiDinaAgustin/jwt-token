package com.example.jwt_token.service;

import com.example.jwt_token.model.Transaction;
import com.example.jwt_token.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    // Get All Transactions
    public List<Transaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

    // Get all Transactions with pagination, sorting, and searching
    public Page<Transaction> getListTransactions(Long transactionCode, Integer page, Integer limit, String sort, String order ) {
        Sort sortBy = "DESC".equalsIgnoreCase(order)
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, limit, sortBy);

        if (transactionCode != null) {
            return transactionRepository.search(transactionCode, pageable);
        }

        return transactionRepository.findAll(pageable);
    }
}
