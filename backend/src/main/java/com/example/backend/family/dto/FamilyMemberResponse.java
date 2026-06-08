package com.example.backend.family.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FamilyMemberResponse {
    private Long familyId;
    private Long memberId;
    private String fullName;
    private Integer age;
    private Boolean isActive;
    private Map<String, String> customFields;
}
