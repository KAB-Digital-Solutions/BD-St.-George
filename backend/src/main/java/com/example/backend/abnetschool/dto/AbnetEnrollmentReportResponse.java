package com.example.backend.abnetschool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AbnetEnrollmentReportResponse {
    private long totalActive;
    private Map<String, Long> byGender;
    private List<AbnetSchoolResponse> students;
}
