package com.example.jwt_token.service;

import com.example.jwt_token.dto.TransactionDetailRequest;
import com.example.jwt_token.dto.TransactionRequest;
import com.example.jwt_token.model.Status;
import com.example.jwt_token.model.Transaction;
import com.example.jwt_token.model.TransactionDetails;
import com.example.jwt_token.repository.TransactionDetailsRepository;
import com.example.jwt_token.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionDetailsRepository transactionDetailsRepository;

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

    // Create Transaction
    public Transaction createTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setCustomerId(request.getCustomerId());
        transaction.setTransactionCode(generateTransactionCode());
        transaction.setStatus(Status.PENDING);
        transaction.setCreatedAt(Instant.now().toEpochMilli());
        transaction.setUpdatedAt(Instant.now().toEpochMilli());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (TransactionDetailRequest d : request.getDetails()) {
            BigDecimal subTotal = d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
            totalAmount = totalAmount.add(subTotal);
        }

        transaction.setAmount(totalAmount);

        Transaction savedTransaction = transactionRepository.save(transaction);

        for (TransactionDetailRequest d : request.getDetails()) {
            TransactionDetails detail = new TransactionDetails();
            detail.setTransaction(savedTransaction);
            detail.setProductId(d.getProductId());
            detail.setQuantity(d.getQuantity());
            detail.setPrice(d.getPrice());
            detail.setSubTotal(d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity())));
            detail.setCreatedAt(Instant.now().toEpochMilli());
            detail.setUpdatedAt(Instant.now().toEpochMilli());
            transactionDetailsRepository.save(detail);
        }

        return savedTransaction;
    }

    // Generate unique transaction code
    private Long generateTransactionCode() {
        return 1000000L + (long) (Math.random() * 9000000L);
    }
}
