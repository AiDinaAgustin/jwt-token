package com.example.jwt_token.repository;

import com.example.jwt_token.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
