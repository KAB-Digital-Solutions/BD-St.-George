package com.example.backend.sundayschool.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class SundaySchoolRequest {

    @NotBlank
    private String fullName;

    private String phone;
    private LocalDate birthDate;
    private String gender;
    private Long memberId;
    private Long clergyId;
    private Map<String, String> customFields;
}
