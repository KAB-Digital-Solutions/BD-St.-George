package com.example.backend.contributions.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class ContributionResponse {
    private Long contribId;
    private Long memberId;
    private BigDecimal amount;
    private String receiptNo;
    private Integer ethiopianYear;
    private LocalDate paymentDate;
    private Boolean isActive;
    private Map<String, String> customFields;
}
