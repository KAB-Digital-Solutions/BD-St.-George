package com.example.backend.workers.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WorkerResponse {
    private Long workerId;
    private String fullName;
    private String phone;
    private String jobRole;
    private String gender;
    private Boolean isActive;
    private Map<String, String> customFields;
}
