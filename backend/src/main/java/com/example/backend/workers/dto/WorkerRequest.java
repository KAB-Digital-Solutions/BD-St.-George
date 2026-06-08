package com.example.backend.workers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

@Data
public class WorkerRequest {

    @NotBlank
    private String fullName;

    private String phone;
    private String jobRole;
    private String gender;
    private Map<String, String> customFields;
}
