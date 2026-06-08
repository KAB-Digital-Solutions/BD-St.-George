package com.example.backend.emergencycontacts.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmergencyContactResponse {
    private Long contactId;
    private String fullName;
    private String phone;
    private String address;
    private Long workerId;
    private Long officeId;
    private Boolean isActive;
    private Map<String, String> customFields;
}
