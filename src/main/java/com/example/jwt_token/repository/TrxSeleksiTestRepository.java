package com.example.jwt_token.repository;

import com.example.jwt_token.model.TrxSeleksiTests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrxSeleksiTestRepository extends JpaRepository<TrxSeleksiTests, Long> {
}
