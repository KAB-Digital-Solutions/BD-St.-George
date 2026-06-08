package com.example.backend.abnetschool.service;

import com.example.backend.abnetschool.dto.AbnetEnrollmentReportResponse;
import com.example.backend.abnetschool.dto.AbnetSchoolRequest;
import com.example.backend.abnetschool.dto.AbnetSchoolResponse;
import com.example.backend.abnetschool.entity.AbnetSchoolStudent;
import com.example.backend.abnetschool.repository.AbnetSchoolRepository;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbnetSchoolService {

    private final AbnetSchoolRepository abnetSchoolRepository;
    private final MemberRepository memberRepository;
    private final ClergyService clergyService;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public AbnetSchoolResponse create(AbnetSchoolRequest request) {
        validateLinks(request);
        AbnetSchoolStudent record = mapToEntity(new AbnetSchoolStudent(), request);
        record = abnetSchoolRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.ABNET_SCHOOL, record.getAbnetId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public AbnetSchoolResponse update(Long id, AbnetSchoolRequest request) {
        AbnetSchoolStudent existing = abnetSchoolRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Abnet school student not found"));
        validateLinks(request);
        AbnetSchoolStudent updated = mapToEntity(existing, request);
        updated = abnetSchoolRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.ABNET_SCHOOL, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public AbnetSchoolResponse get(Long id) {
        AbnetSchoolStudent record = abnetSchoolRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Abnet school student not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<AbnetSchoolResponse> list(Long memberId, Long clergyId, String q, Pageable pageable) {
        return abnetSchoolRepository.search(memberId, clergyId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<AbnetSchoolResponse> listForMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId);
        }
        return abnetSchoolRepository.findByMemberIdAndIsActiveTrueOrderByAbnetIdDesc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public AbnetEnrollmentReportResponse enrollmentReport() {
        List<AbnetSchoolResponse> students = abnetSchoolRepository.findByIsActiveTrueOrderByFullNameAsc().stream()
                .map(this::toResponse)
                .toList();
        var byGender = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getGender() != null && !s.getGender().isBlank() ? s.getGender() : "Unknown",
                        LinkedHashMap::new,
                        Collectors.counting()));
        return AbnetEnrollmentReportResponse.builder()
                .totalActive(students.size())
                .byGender(byGender)
                .students(students)
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        AbnetSchoolStudent record = abnetSchoolRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Abnet school student not found"));
        record.setIsActive(false);
        abnetSchoolRepository.save(record);
    }

    private void validateLinks(AbnetSchoolRequest request) {
        if (request.getMemberId() != null && !memberRepository.existsById(request.getMemberId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + request.getMemberId());
        }
        clergyService.validateExists(request.getClergyId());
    }

    private AbnetSchoolStudent mapToEntity(AbnetSchoolStudent record, AbnetSchoolRequest request) {
        record.setFullName(request.getFullName());
        record.setPhone(request.getPhone());
        record.setBirthDate(request.getBirthDate());
        record.setGender(request.getGender());
        record.setMemberId(request.getMemberId());
        record.setClergyId(request.getClergyId());
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private AbnetSchoolResponse toResponse(AbnetSchoolStudent record) {
        return AbnetSchoolResponse.builder()
                .abnetId(record.getAbnetId())
                .fullName(record.getFullName())
                .phone(record.getPhone())
                .birthDate(record.getBirthDate())
                .gender(record.getGender())
                .memberId(record.getMemberId())
                .clergyId(record.getClergyId())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.ABNET_SCHOOL, record.getAbnetId()))
                .build();
    }
}
