package com.example.backend.clergy.service;

import com.example.backend.clergy.dto.ClergyRequest;
import com.example.backend.clergy.dto.ClergyResponse;
import com.example.backend.clergy.entity.Clergy;
import com.example.backend.clergy.repository.ClergyRepository;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
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
public class ClergyService {

    private final ClergyRepository clergyRepository;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public ClergyResponse create(ClergyRequest request) {
        Clergy clergy = mapToEntity(new Clergy(), request);
        clergy = clergyRepository.save(clergy);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.CLERGY, clergy.getClergyId(), request.getCustomFields());
        }
        return toResponse(clergy);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public ClergyResponse update(Long id, ClergyRequest request) {
        Clergy existing = clergyRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clergy not found"));
        Clergy updated = mapToEntity(existing, request);
        updated = clergyRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.CLERGY, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public ClergyResponse get(Long id) {
        Clergy clergy = clergyRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clergy not found"));
        return toResponse(clergy);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<ClergyResponse> list(String q, Pageable pageable) {
        return clergyRepository.search(q, pageable).map(this::toResponse);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        Clergy clergy = clergyRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Clergy not found"));
        clergy.setIsActive(false);
        clergyRepository.save(clergy);
    }

    @Transactional(readOnly = true)
    public void validateExists(Long clergyId) {
        if (clergyId != null && !clergyRepository.existsById(clergyId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Clergy not found: " + clergyId);
        }
    }

    private Clergy mapToEntity(Clergy clergy, ClergyRequest request) {
        clergy.setFullName(request.getFullName());
        clergy.setRoleType(request.getRoleType());
        clergy.setPhone(request.getPhone());
        clergy.setAddress(request.getAddress());
        clergy.setPhotoPath(request.getPhotoPath());
        if (request.getIsActive() != null) {
            clergy.setIsActive(request.getIsActive());
        }
        if (clergy.getIsActive() == null) {
            clergy.setIsActive(true);
        }
        return clergy;
    }

    private ClergyResponse toResponse(Clergy clergy) {
        return ClergyResponse.builder()
                .clergyId(clergy.getClergyId())
                .fullName(clergy.getFullName())
                .roleType(clergy.getRoleType())
                .phone(clergy.getPhone())
                .address(clergy.getAddress())
                .photoPath(clergy.getPhotoPath())
                .isActive(clergy.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.CLERGY, clergy.getClergyId()))
                .build();
    }
}
