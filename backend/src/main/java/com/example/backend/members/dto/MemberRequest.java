package com.example.backend.members.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class MemberRequest {

    @NotBlank
    private String fullName;

    private String phone;
    private String photoPath;
    private String kebele;
    private Long clergyId;
    private LocalDate registeredDate;
    private Boolean isActive;
    private Map<String, Object> secondMember;
    private Map<String, String> customFields;
}
