package com.example.backend.clergy.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ClergyResponse {
    private Long clergyId;
    private String fullName;
    private String roleType;
    private String phone;
    private String address;
    private String photoPath;
    private Boolean isActive;
    private Map<String, String> customFields;
}
