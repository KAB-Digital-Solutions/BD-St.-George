package com.example.backend.contributions.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class ContributionRequest {

    @NotNull
    private Long memberId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String receiptNo;

    private Integer ethiopianYear;
    private LocalDate paymentDate;
    private Map<String, String> customFields;
}
