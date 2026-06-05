package com.example.backend.auth.dto;

import com.example.backend.auth.domain.UserRole;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private UserRole role;
    private Boolean isActive;
}
