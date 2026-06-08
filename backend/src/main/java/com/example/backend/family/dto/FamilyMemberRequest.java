package com.example.backend.family.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class FamilyMemberRequest {

    @NotNull
    private Long memberId;

    @NotBlank
    private String fullName;

    private Integer age;
    private Map<String, String> customFields;
}
