package com.example.backend.family.service;

import com.example.backend.family.dto.FamilyMemberRequest;
import com.example.backend.family.dto.FamilyMemberResponse;
import com.example.backend.family.entity.FamilyMember;
import com.example.backend.family.repository.FamilyMemberRepository;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.members.repository.MemberRepository;
import com.example.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyMemberService {

    private final FamilyMemberRepository familyMemberRepository;
    private final MemberRepository memberRepository;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public FamilyMemberResponse create(FamilyMemberRequest request) {
        validateMemberExists(request.getMemberId());
        FamilyMember record = mapToEntity(new FamilyMember(), request);
        record = familyMemberRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(
                    ModuleKeys.FAMILY_MEMBERS, record.getFamilyId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public FamilyMemberResponse update(Long id, FamilyMemberRequest request) {
        FamilyMember existing = familyMemberRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family member not found"));
        validateMemberExists(request.getMemberId());
        FamilyMember updated = mapToEntity(existing, request);
        updated = familyMemberRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.FAMILY_MEMBERS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public FamilyMemberResponse get(Long id) {
        FamilyMember record = familyMemberRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family member not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<FamilyMemberResponse> list(Long memberId, String q, Pageable pageable) {
        return familyMemberRepository.search(memberId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<FamilyMemberResponse> listForMember(Long memberId) {
        validateMemberExists(memberId);
        return familyMemberRepository.findByMemberIdAndIsActiveTrueOrderByFamilyIdAsc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        FamilyMember record = familyMemberRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Family member not found"));
        record.setIsActive(false);
        familyMemberRepository.save(record);
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId);
        }
    }

    private FamilyMember mapToEntity(FamilyMember record, FamilyMemberRequest request) {
        record.setMemberId(request.getMemberId());
        record.setFullName(request.getFullName());
        record.setAge(request.getAge());
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private FamilyMemberResponse toResponse(FamilyMember record) {
        return FamilyMemberResponse.builder()
                .familyId(record.getFamilyId())
                .memberId(record.getMemberId())
                .fullName(record.getFullName())
                .age(record.getAge())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.FAMILY_MEMBERS, record.getFamilyId()))
                .build();
    }
}
