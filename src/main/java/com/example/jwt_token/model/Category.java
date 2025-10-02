package com.example.jwt_token.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 2, max = 50)
    private String name;

    @Column(nullable = false, unique = true)
    @Size(min = 2, max = 50)
    private String slug;

    @Column(nullable = false)
    @Size(min= 5, max = 500)
    private String description;

    @OneToMany(mappedBy = "category",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
    fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"category"})
    private List<Product> products;


    public Category(String name) {
        this.name = name;
    }
}
