package com.example.backend.sundayschool.service;

import com.example.backend.clergy.service.ClergyService;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.members.repository.MemberRepository;
import com.example.backend.shared.exception.ApiException;
import com.example.backend.sundayschool.dto.EnrollmentReportResponse;
import com.example.backend.sundayschool.dto.SundaySchoolRequest;
import com.example.backend.sundayschool.dto.SundaySchoolResponse;
import com.example.backend.sundayschool.entity.SundaySchoolStudent;
import com.example.backend.sundayschool.repository.SundaySchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SundaySchoolService {

    private final SundaySchoolRepository sundaySchoolRepository;
    private final MemberRepository memberRepository;
    private final ClergyService clergyService;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public SundaySchoolResponse create(SundaySchoolRequest request) {
        validateLinks(request);
        SundaySchoolStudent record = mapToEntity(new SundaySchoolStudent(), request);
        record = sundaySchoolRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.SUNDAY_SCHOOL, record.getSsId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public SundaySchoolResponse update(Long id, SundaySchoolRequest request) {
        SundaySchoolStudent existing = sundaySchoolRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sunday school student not found"));
        validateLinks(request);
        SundaySchoolStudent updated = mapToEntity(existing, request);
        updated = sundaySchoolRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.SUNDAY_SCHOOL, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public SundaySchoolResponse get(Long id) {
        SundaySchoolStudent record = sundaySchoolRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sunday school student not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<SundaySchoolResponse> list(Long memberId, Long clergyId, String q, Pageable pageable) {
        return sundaySchoolRepository.search(memberId, clergyId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<SundaySchoolResponse> listForMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId);
        }
        return sundaySchoolRepository.findByMemberIdAndIsActiveTrueOrderBySsIdDesc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public EnrollmentReportResponse enrollmentReport() {
        List<SundaySchoolResponse> students = sundaySchoolRepository.findByIsActiveTrueOrderByFullNameAsc().stream()
                .map(this::toResponse)
                .toList();
        Map<String, Long> byGender = students.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getGender() != null && !s.getGender().isBlank() ? s.getGender() : "Unknown",
                        LinkedHashMap::new,
                        Collectors.counting()));
        return EnrollmentReportResponse.builder()
                .totalActive(students.size())
                .byGender(byGender)
                .students(students)
                .build();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        SundaySchoolStudent record = sundaySchoolRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Sunday school student not found"));
        record.setIsActive(false);
        sundaySchoolRepository.save(record);
    }

    private void validateLinks(SundaySchoolRequest request) {
        if (request.getMemberId() != null && !memberRepository.existsById(request.getMemberId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + request.getMemberId());
        }
        clergyService.validateExists(request.getClergyId());
    }

    private SundaySchoolStudent mapToEntity(SundaySchoolStudent record, SundaySchoolRequest request) {
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

    private SundaySchoolResponse toResponse(SundaySchoolStudent record) {
        return SundaySchoolResponse.builder()
                .ssId(record.getSsId())
                .fullName(record.getFullName())
                .phone(record.getPhone())
                .birthDate(record.getBirthDate())
                .gender(record.getGender())
                .memberId(record.getMemberId())
                .clergyId(record.getClergyId())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.SUNDAY_SCHOOL, record.getSsId()))
                .build();
    }
}
