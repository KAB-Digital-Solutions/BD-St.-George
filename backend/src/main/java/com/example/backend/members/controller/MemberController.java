package com.example.backend.members.controller;

import com.example.backend.baptisms.dto.BaptismResponse;
import com.example.backend.baptisms.service.BaptismService;
import com.example.backend.abnetschool.dto.AbnetSchoolResponse;
import com.example.backend.abnetschool.service.AbnetSchoolService;
import com.example.backend.contributions.dto.ContributionResponse;
import com.example.backend.contributions.service.ContributionService;
import com.example.backend.deceased.dto.DeceasedResponse;
import com.example.backend.deceased.service.DeceasedService;
import com.example.backend.transfers.dto.TransferResponse;
import com.example.backend.transfers.service.TransferService;
import com.example.backend.officestaff.dto.OfficeStaffResponse;
import com.example.backend.officestaff.service.OfficeStaffService;
import com.example.backend.sundayschool.dto.SundaySchoolResponse;
import com.example.backend.sundayschool.service.SundaySchoolService;
import com.example.backend.staffministers.dto.StaffMinisterResponse;
import com.example.backend.staffministers.service.StaffMinisterService;
import com.example.backend.family.dto.FamilyMemberResponse;
import com.example.backend.family.service.FamilyMemberService;
import com.example.backend.members.dto.MemberProfileResponse;
import com.example.backend.members.dto.MemberRequest;
import com.example.backend.members.dto.MemberResponse;
import com.example.backend.members.service.MemberService;
import com.example.backend.shared.dto.PageResponse;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Central member registry (fixed fields + dynamic custom fields)")
public class MemberController {

    private final MemberService memberService;
    private final FamilyMemberService familyMemberService;
    private final BaptismService baptismService;
    private final StaffMinisterService staffMinisterService;
    private final OfficeStaffService officeStaffService;
    private final SundaySchoolService sundaySchoolService;
    private final AbnetSchoolService abnetSchoolService;
    private final ContributionService contributionService;
    private final TransferService transferService;
    private final DeceasedService deceasedService;

    @Operation(summary = "Create member", description = "Saves core fields and customFields in one request")
    @PostMapping
    public MemberResponse create(@Valid @RequestBody MemberRequest request) {
        return memberService.create(request);
    }

    @Operation(summary = "List members", description = "Search by name, phone, kebele, or member ID")
    @GetMapping
    public PageResponse<MemberResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("memberId").descending());
        return PageResponse.from(memberService.list(q, pageable));
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return memberService.get(id);
    }

    @Operation(summary = "Member profile", description = "Member with spiritual father, household, and baptisms")
    @GetMapping("/{id}/profile")
    public MemberProfileResponse profile(@PathVariable Long id) {
        return memberService.getProfile(id);
    }

    @Operation(summary = "List household for member", description = "Active family members linked to this member")
    @GetMapping("/{id}/family-members")
    public List<FamilyMemberResponse> familyMembers(@PathVariable Long id) {
        return familyMemberService.listForMember(id);
    }

    @Operation(summary = "List baptisms for member", description = "Active baptism records for children linked to this member")
    @GetMapping("/{id}/baptisms")
    public List<BaptismResponse> baptisms(@PathVariable Long id) {
        return baptismService.listForMember(id);
    }

    @Operation(summary = "List staff minister records for member", description = "Active staff roles linked to this member")
    @GetMapping("/{id}/staff-ministers")
    public List<StaffMinisterResponse> staffMinisters(@PathVariable Long id) {
        return staffMinisterService.listForMember(id);
    }

    @Operation(summary = "List office staff for member", description = "Active office roles linked to this member")
    @GetMapping("/{id}/office-staff")
    public List<OfficeStaffResponse> officeStaff(@PathVariable Long id) {
        return officeStaffService.listForMember(id);
    }

    @Operation(summary = "Sunday school enrollments for member")
    @GetMapping("/{id}/sunday-school")
    public List<SundaySchoolResponse> sundaySchool(@PathVariable Long id) {
        return sundaySchoolService.listForMember(id);
    }

    @Operation(summary = "Abnet school enrollments for member")
    @GetMapping("/{id}/abnet-school")
    public List<AbnetSchoolResponse> abnetSchool(@PathVariable Long id) {
        return abnetSchoolService.listForMember(id);
    }

    @Operation(summary = "Contributions for member")
    @GetMapping("/{id}/contributions")
    public List<ContributionResponse> contributions(@PathVariable Long id) {
        return contributionService.listForMember(id);
    }

    @Operation(summary = "Transfer records for member")
    @GetMapping("/{id}/transfers")
    public List<TransferResponse> transfers(@PathVariable Long id) {
        return transferService.listForMember(id);
    }

    @Operation(summary = "Deceased archive for member", description = "404 if member has no deceased record")
    @GetMapping("/{id}/deceased")
    public DeceasedResponse deceased(@PathVariable Long id) {
        return deceasedService.getByMemberId(id);
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        memberService.deactivate(id);
    }
}
