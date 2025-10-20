package com.example.jwt_token.repository;

import com.example.jwt_token.model.Peserta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PesertaRepository extends JpaRepository<Peserta, Long> {
}
