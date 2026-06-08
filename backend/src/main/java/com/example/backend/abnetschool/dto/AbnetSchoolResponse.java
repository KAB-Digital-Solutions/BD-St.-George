package com.example.backend.abnetschool.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class AbnetSchoolResponse {
    private Long abnetId;
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private Long memberId;
    private Long clergyId;
    private Boolean isActive;
    private Map<String, String> customFields;
}
