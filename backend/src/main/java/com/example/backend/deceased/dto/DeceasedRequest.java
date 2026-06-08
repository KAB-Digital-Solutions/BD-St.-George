package com.example.backend.deceased.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class DeceasedRequest {

    @NotNull
    private Long memberId;

    private LocalDate deathDate;
    private String kebele;
    private String gender;
    private String fullNameDisplay;
    private Map<String, String> customFields;
}
