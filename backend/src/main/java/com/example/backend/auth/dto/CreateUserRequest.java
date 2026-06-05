package com.example.backend.auth.dto;

import com.example.backend.auth.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    @Size(max = 60)
    private String username;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    private String fullName;

    @NotNull
    private UserRole role;
}
