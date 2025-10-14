package com.example.jwt_token.dto;

import com.example.jwt_token.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String fullName;
    private String username;
    private Role role;
}
