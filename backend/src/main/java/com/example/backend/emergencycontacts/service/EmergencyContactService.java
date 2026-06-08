package com.example.backend.emergencycontacts.service;

import com.example.backend.emergencycontacts.dto.EmergencyContactRequest;
import com.example.backend.emergencycontacts.dto.EmergencyContactResponse;
import com.example.backend.emergencycontacts.entity.EmergencyContact;
import com.example.backend.emergencycontacts.repository.EmergencyContactRepository;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.officestaff.repository.OfficeStaffRepository;
import com.example.backend.shared.exception.ApiException;
import com.example.backend.workers.service.WorkerService;
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
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final OfficeStaffRepository officeStaffRepository;
    private final WorkerService workerService;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public EmergencyContactResponse create(EmergencyContactRequest request) {
        validateLinks(request);
        EmergencyContact record = mapToEntity(new EmergencyContact(), request);
        record = emergencyContactRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(
                    ModuleKeys.EMERGENCY_CONTACTS, record.getContactId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public EmergencyContactResponse update(Long id, EmergencyContactRequest request) {
        EmergencyContact existing = emergencyContactRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Emergency contact not found"));
        validateLinks(request);
        EmergencyContact updated = mapToEntity(existing, request);
        updated = emergencyContactRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.EMERGENCY_CONTACTS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public EmergencyContactResponse get(Long id) {
        EmergencyContact record = emergencyContactRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Emergency contact not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<EmergencyContactResponse> list(Long workerId, Long officeId, String q, Pageable pageable) {
        return emergencyContactRepository.search(workerId, officeId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<EmergencyContactResponse> listForWorker(Long workerId) {
        workerService.validateExists(workerId);
        return emergencyContactRepository.findByWorkerIdAndIsActiveTrueOrderByContactIdDesc(workerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<EmergencyContactResponse> listForOfficeStaff(Long officeId) {
        if (!officeStaffRepository.existsById(officeId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Office staff not found: " + officeId);
        }
        return emergencyContactRepository.findByOfficeIdAndIsActiveTrueOrderByContactIdDesc(officeId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        EmergencyContact record = emergencyContactRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Emergency contact not found"));
        record.setIsActive(false);
        emergencyContactRepository.save(record);
    }

    private void validateLinks(EmergencyContactRequest request) {
        workerService.validateExists(request.getWorkerId());
        if (request.getOfficeId() != null && !officeStaffRepository.existsById(request.getOfficeId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Office staff not found: " + request.getOfficeId());
        }
        if (request.getWorkerId() == null && request.getOfficeId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Link to workerId or officeId is required");
        }
    }

    private EmergencyContact mapToEntity(EmergencyContact record, EmergencyContactRequest request) {
        record.setFullName(request.getFullName());
        record.setPhone(request.getPhone());
        record.setAddress(request.getAddress());
        record.setWorkerId(request.getWorkerId());
        record.setOfficeId(request.getOfficeId());
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private EmergencyContactResponse toResponse(EmergencyContact record) {
        return EmergencyContactResponse.builder()
                .contactId(record.getContactId())
                .fullName(record.getFullName())
                .phone(record.getPhone())
                .address(record.getAddress())
                .workerId(record.getWorkerId())
                .officeId(record.getOfficeId())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.EMERGENCY_CONTACTS, record.getContactId()))
                .build();
    }
}
