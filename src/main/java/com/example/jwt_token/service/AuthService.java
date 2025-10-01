package com.example.jwt_token.service;

import com.example.jwt_token.dto.RegisterRequest;
import com.example.jwt_token.model.Role;
import com.example.jwt_token.model.User;
import com.example.jwt_token.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }
        // Create new user with encoded password and default role USER
        User user = User
                .builder()
                .fullName(registerRequest.getFullName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();
        userRepository.save(user);
    }
}