package com.example.backend.sundayschool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class EnrollmentReportResponse {
    private long totalActive;
    private Map<String, Long> byGender;
    private List<SundaySchoolResponse> students;
}
