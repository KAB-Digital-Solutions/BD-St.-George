package com.example.backend.officestaff.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class OfficeStaffResponse {
    private Long officeId;
    private String fullName;
    private String position;
    private String phone;
    private Long memberId;
    private Long clergyId;
    private Boolean isActive;
    private Map<String, String> customFields;
}
