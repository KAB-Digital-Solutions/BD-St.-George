package com.example.backend.staffministers.service;

import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.members.repository.MemberRepository;
import com.example.backend.shared.exception.ApiException;
import com.example.backend.staffministers.dto.StaffMinisterRequest;
import com.example.backend.staffministers.dto.StaffMinisterResponse;
import com.example.backend.staffministers.entity.StaffMinister;
import com.example.backend.staffministers.repository.StaffMinisterRepository;
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
public class StaffMinisterService {

    private final StaffMinisterRepository staffMinisterRepository;
    private final MemberRepository memberRepository;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public StaffMinisterResponse create(StaffMinisterRequest request) {
        validateMemberExists(request.getMemberId());
        StaffMinister record = mapToEntity(new StaffMinister(), request);
        record = staffMinisterRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(
                    ModuleKeys.STAFF_MINISTERS, record.getStaffId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public StaffMinisterResponse update(Long id, StaffMinisterRequest request) {
        StaffMinister existing = staffMinisterRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Staff minister not found"));
        validateMemberExists(request.getMemberId());
        StaffMinister updated = mapToEntity(existing, request);
        updated = staffMinisterRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.STAFF_MINISTERS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public StaffMinisterResponse get(Long id) {
        StaffMinister record = staffMinisterRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Staff minister not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<StaffMinisterResponse> list(Long memberId, String q, Pageable pageable) {
        return staffMinisterRepository.search(memberId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<StaffMinisterResponse> listForMember(Long memberId) {
        validateMemberExists(memberId);
        return staffMinisterRepository.findByMemberIdAndIsActiveTrueOrderByStaffIdDesc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        StaffMinister record = staffMinisterRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Staff minister not found"));
        record.setIsActive(false);
        staffMinisterRepository.save(record);
    }

    private void validateMemberExists(Long memberId) {
        if (memberId != null && !memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId);
        }
    }

    private StaffMinister mapToEntity(StaffMinister record, StaffMinisterRequest request) {
        record.setFullName(request.getFullName());
        record.setGender(request.getGender());
        record.setPhone(request.getPhone());
        record.setHireDate(request.getHireDate());
        record.setEmploymentType(request.getEmploymentType());
        record.setMemberId(request.getMemberId());
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private StaffMinisterResponse toResponse(StaffMinister record) {
        return StaffMinisterResponse.builder()
                .staffId(record.getStaffId())
                .fullName(record.getFullName())
                .gender(record.getGender())
                .phone(record.getPhone())
                .hireDate(record.getHireDate())
                .employmentType(record.getEmploymentType())
                .memberId(record.getMemberId())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.STAFF_MINISTERS, record.getStaffId()))
                .build();
    }
}
