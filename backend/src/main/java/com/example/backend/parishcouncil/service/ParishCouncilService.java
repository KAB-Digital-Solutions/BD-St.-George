package com.example.backend.parishcouncil.service;

import com.example.backend.clergy.service.ClergyService;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.parishcouncil.dto.ParishCouncilRequest;
import com.example.backend.parishcouncil.dto.ParishCouncilResponse;
import com.example.backend.parishcouncil.entity.ParishCouncilMember;
import com.example.backend.parishcouncil.repository.ParishCouncilRepository;
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
public class ParishCouncilService {

    private final ParishCouncilRepository parishCouncilRepository;
    private final ClergyService clergyService;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public ParishCouncilResponse create(ParishCouncilRequest request) {
        clergyService.validateExists(request.getClergyId());
        ParishCouncilMember record = mapToEntity(new ParishCouncilMember(), request);
        record = parishCouncilRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.PARISH_COUNCIL, record.getCouncilId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public ParishCouncilResponse update(Long id, ParishCouncilRequest request) {
        ParishCouncilMember existing = parishCouncilRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Parish council member not found"));
        clergyService.validateExists(request.getClergyId());
        ParishCouncilMember updated = mapToEntity(existing, request);
        updated = parishCouncilRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.PARISH_COUNCIL, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public ParishCouncilResponse get(Long id) {
        ParishCouncilMember record = parishCouncilRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Parish council member not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<ParishCouncilResponse> list(Long clergyId, String q, Pageable pageable) {
        return parishCouncilRepository.search(clergyId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<ParishCouncilResponse> listForClergy(Long clergyId) {
        clergyService.validateExists(clergyId);
        return parishCouncilRepository.findByClergyIdAndIsActiveTrueOrderByCouncilIdDesc(clergyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        ParishCouncilMember record = parishCouncilRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Parish council member not found"));
        record.setIsActive(false);
        parishCouncilRepository.save(record);
    }

    private ParishCouncilMember mapToEntity(ParishCouncilMember record, ParishCouncilRequest request) {
        record.setFullName(request.getFullName());
        record.setPosition(request.getPosition());
        record.setPhone(request.getPhone());
        record.setClergyId(request.getClergyId());
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private ParishCouncilResponse toResponse(ParishCouncilMember record) {
        return ParishCouncilResponse.builder()
                .councilId(record.getCouncilId())
                .fullName(record.getFullName())
                .position(record.getPosition())
                .phone(record.getPhone())
                .clergyId(record.getClergyId())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.PARISH_COUNCIL, record.getCouncilId()))
                .build();
    }
}
