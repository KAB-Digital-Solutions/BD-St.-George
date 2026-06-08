package com.example.backend.baptisms.service;

import com.example.backend.baptisms.dto.BaptismRequest;
import com.example.backend.baptisms.dto.BaptismResponse;
import com.example.backend.baptisms.entity.Baptism;
import com.example.backend.baptisms.repository.BaptismRepository;
import com.example.backend.clergy.service.ClergyService;
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
public class BaptismService {

    private final BaptismRepository baptismRepository;
    private final MemberRepository memberRepository;
    private final ClergyService clergyService;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public BaptismResponse create(BaptismRequest request) {
        validateMemberExists(request.getMemberId());
        clergyService.validateExists(request.getOfficiatingClergyId());
        Baptism record = mapToEntity(new Baptism(), request);
        record = baptismRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.BAPTISMS, record.getBaptismId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public BaptismResponse update(Long id, BaptismRequest request) {
        Baptism existing = baptismRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Baptism record not found"));
        validateMemberExists(request.getMemberId());
        clergyService.validateExists(request.getOfficiatingClergyId());
        Baptism updated = mapToEntity(existing, request);
        updated = baptismRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.BAPTISMS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public BaptismResponse get(Long id) {
        Baptism record = baptismRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Baptism record not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<BaptismResponse> list(Long memberId, String q, Pageable pageable) {
        return baptismRepository.search(memberId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<BaptismResponse> listForMember(Long memberId) {
        validateMemberExists(memberId);
        return baptismRepository.findByMemberIdAndIsActiveTrueOrderByBaptismDateDescBaptismIdDesc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        Baptism record = baptismRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Baptism record not found"));
        record.setIsActive(false);
        baptismRepository.save(record);
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId);
        }
    }

    private Baptism mapToEntity(Baptism record, BaptismRequest request) {
        record.setMemberId(request.getMemberId());
        record.setChildName(request.getChildName());
        record.setBaptismDate(request.getBaptismDate());
        record.setOfficiatingClergyId(request.getOfficiatingClergyId());
        record.setChurchName(request.getChurchName());
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private BaptismResponse toResponse(Baptism record) {
        return BaptismResponse.builder()
                .baptismId(record.getBaptismId())
                .memberId(record.getMemberId())
                .childName(record.getChildName())
                .baptismDate(record.getBaptismDate())
                .officiatingClergyId(record.getOfficiatingClergyId())
                .churchName(record.getChurchName())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.BAPTISMS, record.getBaptismId()))
                .build();
    }
}
