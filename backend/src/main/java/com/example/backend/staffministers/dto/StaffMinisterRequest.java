package com.example.backend.staffministers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class StaffMinisterRequest {

    @NotBlank
    private String fullName;

    private String gender;
    private String phone;
    private LocalDate hireDate;
    private String employmentType;
    private Long memberId;
    private Map<String, String> customFields;
}
