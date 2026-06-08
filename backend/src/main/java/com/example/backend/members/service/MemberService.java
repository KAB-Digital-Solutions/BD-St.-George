package com.example.backend.members.service;

import com.example.backend.baptisms.service.BaptismService;
import com.example.backend.clergy.service.ClergyService;
import com.example.backend.family.service.FamilyMemberService;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.members.domain.MemberStatus;
import com.example.backend.members.dto.MemberProfileResponse;
import com.example.backend.members.dto.MemberRequest;
import com.example.backend.members.dto.MemberResponse;
import com.example.backend.members.entity.Member;
import com.example.backend.members.repository.MemberRepository;
import com.example.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ClergyService clergyService;
    private final FamilyMemberService familyMemberService;
    private final BaptismService baptismService;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public MemberResponse create(MemberRequest request) {
        clergyService.validateExists(request.getClergyId());
        Member member = mapToEntity(new Member(), request);
        if (member.getRegisteredDate() == null) {
            member.setRegisteredDate(java.time.LocalDate.now());
        }
        member = memberRepository.save(member);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.MEMBERS, member.getMemberId(), request.getCustomFields());
        }
        return toResponse(member);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public MemberResponse update(Long id, MemberRequest request) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Member not found"));
        clergyService.validateExists(request.getClergyId());
        Member updated = mapToEntity(existing, request);
        updated = memberRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.MEMBERS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public MemberResponse get(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Member not found"));
        return toResponse(member);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<MemberResponse> list(String q, Pageable pageable) {
        return memberRepository.search(q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public MemberProfileResponse getProfile(Long id) {
        MemberResponse member = get(id);
        var spiritualFather = member.getClergyId() != null
                ? clergyService.get(member.getClergyId())
                : null;
        return MemberProfileResponse.builder()
                .member(member)
                .spiritualFather(spiritualFather)
                .familyMembers(familyMemberService.listForMember(id))
                .baptisms(baptismService.listForMember(id))
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Member not found"));
        member.setIsActive(false);
        memberRepository.save(member);
    }

    private Member mapToEntity(Member member, MemberRequest request) {
        member.setFullName(request.getFullName());
        member.setPhone(request.getPhone());
        member.setPhotoPath(request.getPhotoPath());
        member.setKebele(request.getKebele());
        member.setClergyId(request.getClergyId());
        if (request.getRegisteredDate() != null) {
            member.setRegisteredDate(request.getRegisteredDate());
        }
        if (request.getIsActive() != null) {
            member.setIsActive(request.getIsActive());
        }
        member.setSecondMember(request.getSecondMember());
        if (member.getStatus() == null) {
            member.setStatus(MemberStatus.ACTIVE);
        }
        return member;
    }

    private MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .fullName(member.getFullName())
                .phone(member.getPhone())
                .photoPath(member.getPhotoPath())
                .kebele(member.getKebele())
                .clergyId(member.getClergyId())
                .registeredDate(member.getRegisteredDate())
                .isActive(member.getIsActive())
                .status(member.getStatus())
                .secondMember(member.getSecondMember())
                .customFields(customFieldService.getValues(ModuleKeys.MEMBERS, member.getMemberId()))
                .build();
    }
}
