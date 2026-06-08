package com.example.backend.baptisms.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class BaptismResponse {
    private Long baptismId;
    private Long memberId;
    private String childName;
    private LocalDate baptismDate;
    private Long officiatingClergyId;
    private String churchName;
    private Boolean isActive;
    private Map<String, String> customFields;
}
