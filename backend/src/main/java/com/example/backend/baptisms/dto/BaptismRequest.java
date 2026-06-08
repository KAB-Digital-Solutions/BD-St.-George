package com.example.backend.baptisms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class BaptismRequest {

    @NotNull
    private Long memberId;

    @NotBlank
    private String childName;

    private LocalDate baptismDate;
    private Long officiatingClergyId;
    private String churchName;
    private Map<String, String> customFields;
}
