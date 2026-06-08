package com.example.backend.sundayschool.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class SundaySchoolResponse {
    private Long ssId;
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private Long memberId;
    private Long clergyId;
    private Boolean isActive;
    private Map<String, String> customFields;
}
