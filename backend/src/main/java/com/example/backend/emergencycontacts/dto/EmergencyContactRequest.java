package com.example.backend.emergencycontacts.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class EmergencyContactRequest {

    @NotBlank
    private String fullName;

    private String phone;
    private String address;
    private Long workerId;
    private Long officeId;
    private Map<String, String> customFields;
}
