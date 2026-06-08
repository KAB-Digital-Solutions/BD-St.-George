package com.example.backend.staffministers.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class StaffMinisterResponse {
    private Long staffId;
    private String fullName;
    private String gender;
    private String phone;
    private LocalDate hireDate;
    private String employmentType;
    private Long memberId;
    private Boolean isActive;
    private Map<String, String> customFields;
}
