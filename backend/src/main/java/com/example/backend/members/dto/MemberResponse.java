package com.example.backend.members.dto;

import com.example.backend.members.domain.MemberStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class MemberResponse {
    private Long memberId;
    private String fullName;
    private String phone;
    private String photoPath;
    private String kebele;
    private Long clergyId;
    private LocalDate registeredDate;
    private Boolean isActive;
    private MemberStatus status;
    private Map<String, Object> secondMember;
    private Map<String, String> customFields;
}
