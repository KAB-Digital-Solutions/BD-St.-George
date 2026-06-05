package com.example.backend.auth.dto;

import com.example.backend.auth.domain.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private UserRole role;
    private String fullName;
    private String username;
}
