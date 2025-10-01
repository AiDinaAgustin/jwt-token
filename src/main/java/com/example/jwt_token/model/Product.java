package com.example.jwt_token.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

//    @NotNull
//    @Column(nullable = false)
//    private int quantity;
//
//    @NotNull
//    private Long categoryId;

    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }
}
