package com.example.backend.transfers.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
public class TransferRequest {

    @NotNull
    private Long memberId;

    private String reason;
    private String region;
    private String diocese;
    private String woreda;
    private LocalDate transferDate;
    private Map<String, String> customFields;
}
