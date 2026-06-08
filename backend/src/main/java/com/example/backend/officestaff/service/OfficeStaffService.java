package com.example.backend.officestaff.service;

import com.example.backend.clergy.service.ClergyService;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.members.repository.MemberRepository;
import com.example.backend.officestaff.dto.OfficeStaffRequest;
import com.example.backend.officestaff.dto.OfficeStaffResponse;
import com.example.backend.officestaff.entity.OfficeStaff;
import com.example.backend.officestaff.repository.OfficeStaffRepository;
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
public class OfficeStaffService {

    private final OfficeStaffRepository officeStaffRepository;
    private final MemberRepository memberRepository;
    private final ClergyService clergyService;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public OfficeStaffResponse create(OfficeStaffRequest request) {
        validateLinks(request);
        OfficeStaff record = mapToEntity(new OfficeStaff(), request);
        record = officeStaffRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.OFFICE_STAFF, record.getOfficeId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public OfficeStaffResponse update(Long id, OfficeStaffRequest request) {
        OfficeStaff existing = officeStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Office staff not found"));
        validateLinks(request);
        OfficeStaff updated = mapToEntity(existing, request);
        updated = officeStaffRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.OFFICE_STAFF, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public OfficeStaffResponse get(Long id) {
        OfficeStaff record = officeStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Office staff not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<OfficeStaffResponse> list(Long memberId, Long clergyId, String q, Pageable pageable) {
        return officeStaffRepository.search(memberId, clergyId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<OfficeStaffResponse> listForMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId);
        }
        return officeStaffRepository.findByMemberIdAndIsActiveTrueOrderByOfficeIdDesc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        OfficeStaff record = officeStaffRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Office staff not found"));
        record.setIsActive(false);
        officeStaffRepository.save(record);
    }

    private void validateLinks(OfficeStaffRequest request) {
        if (request.getMemberId() != null && !memberRepository.existsById(request.getMemberId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + request.getMemberId());
        }
        clergyService.validateExists(request.getClergyId());
    }

    private OfficeStaff mapToEntity(OfficeStaff record, OfficeStaffRequest request) {
        record.setFullName(request.getFullName());
        record.setPosition(request.getPosition());
        record.setPhone(request.getPhone());
        record.setMemberId(request.getMemberId());
        record.setClergyId(request.getClergyId());
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private OfficeStaffResponse toResponse(OfficeStaff record) {
        return OfficeStaffResponse.builder()
                .officeId(record.getOfficeId())
                .fullName(record.getFullName())
                .position(record.getPosition())
                .phone(record.getPhone())
                .memberId(record.getMemberId())
                .clergyId(record.getClergyId())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.OFFICE_STAFF, record.getOfficeId()))
                .build();
    }
}
