package com.example.jwt_token.dto;

import lombok.Data;

import java.util.List;

@Data
public class TransactionRequest {

    private Long customerId;
    private List<TransactionDetailRequest> details;
}
