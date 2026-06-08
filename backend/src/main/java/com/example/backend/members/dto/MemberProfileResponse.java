package com.example.backend.members.dto;

import com.example.backend.clergy.dto.ClergyResponse;
import com.example.backend.family.dto.FamilyMemberResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberProfileResponse {
    private MemberResponse member;
    private ClergyResponse spiritualFather;
    private List<FamilyMemberResponse> familyMembers;
}
