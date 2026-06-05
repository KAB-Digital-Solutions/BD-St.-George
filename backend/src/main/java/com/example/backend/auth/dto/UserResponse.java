package com.example.backend.auth.dto;

import com.example.backend.auth.domain.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
}
