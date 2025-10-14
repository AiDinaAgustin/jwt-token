package com.example.jwt_token.controller;

import com.example.jwt_token.dto.TransactionRequest;
import com.example.jwt_token.response.ApiResponse;
import com.example.jwt_token.response.PaginatedResult;
import com.example.jwt_token.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    // Get All Transactions
    @GetMapping
    public ResponseEntity<?> getAllTransactions(){
        var transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(new ApiResponse<>("Transactions retrieved successfully", HttpStatus.OK, transactions));
    }

    // Get All Transactions with pagination, sorting, and searching
    @GetMapping("/list")
    public ResponseEntity<?> getListTransactions(
            @RequestParam(required = false) Long transactionCode,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String order ){

        var transactions = transactionService.getListTransactions(transactionCode, page, limit, sort, order);

        PaginatedResult<?> paginated = new PaginatedResult<>(transactions);
        return ResponseEntity.ok(new ApiResponse<>("Transactions retrieved successfully", HttpStatus.OK, paginated));
    }

    // Get Transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        var transaction = transactionService.getTransactionById(id);
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Transaction not found", HttpStatus.NOT_FOUND));
        }
        return ResponseEntity.ok(new ApiResponse<>("Transaction retrieved successfully", HttpStatus.OK, transaction));
    }

    // Create
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request){
        var transaction = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Transaction created successfully", HttpStatus.CREATED, transaction));
    }
}
