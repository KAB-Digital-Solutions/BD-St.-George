package com.example.backend.parishcouncil.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ParishCouncilResponse {
    private Long councilId;
    private String fullName;
    private String position;
    private String phone;
    private Long clergyId;
    private Boolean isActive;
    private Map<String, String> customFields;
}
