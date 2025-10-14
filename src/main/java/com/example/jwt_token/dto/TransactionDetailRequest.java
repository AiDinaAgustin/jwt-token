package com.example.jwt_token.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDetailRequest {

    private Long productId;
    private Integer quantity;
    private BigDecimal price;
}
