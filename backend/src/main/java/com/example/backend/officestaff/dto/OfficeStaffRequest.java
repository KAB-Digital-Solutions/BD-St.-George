package com.example.backend.officestaff.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class OfficeStaffRequest {

    @NotBlank
    private String fullName;

    private String position;
    private String phone;
    private Long memberId;
    private Long clergyId;
    private Map<String, String> customFields;
}
