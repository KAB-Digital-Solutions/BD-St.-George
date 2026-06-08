package com.example.backend.formengine.dto;

import com.example.backend.formengine.domain.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FieldDefinitionRequest {

    @NotBlank
    private String moduleKey;

    @NotBlank
    private String fieldKey;

    @NotBlank
    private String labelAm;

    private String labelEn;

    @NotNull
    private FieldType fieldType;

    private List<String> dropdownOpts;

    private Map<String, Object> validationRules;

    private Boolean isRequired;

    private Boolean isActive;

    private Integer sortOrder;
}
