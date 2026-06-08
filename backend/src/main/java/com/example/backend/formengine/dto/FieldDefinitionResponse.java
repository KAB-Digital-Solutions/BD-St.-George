package com.example.backend.formengine.dto;

import com.example.backend.formengine.domain.FieldType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class FieldDefinitionResponse {
    private Long id;
    private String moduleKey;
    private String fieldKey;
    private String labelAm;
    private String labelEn;
    private FieldType fieldType;
    private List<String> dropdownOpts;
    private Map<String, Object> validationRules;
    private Boolean isRequired;
    private Boolean isActive;
    private Boolean isSystemCore;
    private Integer sortOrder;
}
