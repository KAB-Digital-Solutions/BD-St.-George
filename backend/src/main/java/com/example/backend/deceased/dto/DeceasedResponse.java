package com.example.backend.deceased.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class DeceasedResponse {
    private Long deceasedId;
    private Long memberId;
    private LocalDate deathDate;
    private String kebele;
    private String gender;
    private String fullNameDisplay;
    private Map<String, String> customFields;
}
