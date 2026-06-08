package com.example.backend.parishcouncil.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class ParishCouncilRequest {

    @NotBlank
    private String fullName;

    private String position;
    private String phone;
    private Long clergyId;
    private Map<String, String> customFields;
}
