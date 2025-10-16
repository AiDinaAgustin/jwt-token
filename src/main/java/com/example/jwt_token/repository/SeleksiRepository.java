package com.example.jwt_token.repository;

import com.example.jwt_token.model.Seleksi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeleksiRepository extends JpaRepository<Seleksi, Long> {
    
}
