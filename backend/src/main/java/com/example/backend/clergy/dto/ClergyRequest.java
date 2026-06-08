package com.example.backend.clergy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class ClergyRequest {

    @NotBlank
    private String fullName;

    private String roleType;
    private String phone;
    private String address;
    private String photoPath;
    private Boolean isActive;
    private Map<String, String> customFields;
}
