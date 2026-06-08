package com.example.backend.transfers.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class TransferResponse {
    private Long transferId;
    private Long memberId;
    private String reason;
    private String region;
    private String diocese;
    private String woreda;
    private LocalDate transferDate;
    private Boolean isActive;
    private Map<String, String> customFields;
}
