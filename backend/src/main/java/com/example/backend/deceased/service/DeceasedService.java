package com.example.backend.deceased.service;

import com.example.backend.deceased.dto.DeceasedRequest;
import com.example.backend.deceased.dto.DeceasedResponse;
import com.example.backend.deceased.entity.DeceasedRecord;
import com.example.backend.deceased.repository.DeceasedRepository;
import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.members.domain.MemberStatus;
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
public class DeceasedService {

    private final DeceasedRepository deceasedRepository;
    private final MemberRepository memberRepository;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public DeceasedResponse create(DeceasedRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + request.getMemberId()));
        if (deceasedRepository.existsByMemberId(request.getMemberId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Deceased record already exists for member: " + request.getMemberId());
        }
        DeceasedRecord record = DeceasedRecord.builder()
                .memberId(request.getMemberId())
                .deathDate(request.getDeathDate() != null ? request.getDeathDate() : java.time.LocalDate.now())
                .kebele(request.getKebele() != null ? request.getKebele() : member.getKebele())
                .gender(request.getGender())
                .fullNameDisplay(request.getFullNameDisplay() != null ? request.getFullNameDisplay() : member.getFullName())
                .build();
        record = deceasedRepository.save(record);
        member.setStatus(MemberStatus.DECEASED);
        member.setIsActive(false);
        memberRepository.save(member);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.DECEASED, record.getDeceasedId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public DeceasedResponse get(Long id) {
        DeceasedRecord record = deceasedRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Deceased record not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public DeceasedResponse getByMemberId(Long memberId) {
        DeceasedRecord record = deceasedRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No deceased record for member: " + memberId));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<DeceasedResponse> list(String q, Pageable pageable) {
        return deceasedRepository.search(q, pageable).map(this::toResponse);
    }

    private DeceasedResponse toResponse(DeceasedRecord record) {
        return DeceasedResponse.builder()
                .deceasedId(record.getDeceasedId())
                .memberId(record.getMemberId())
                .deathDate(record.getDeathDate())
                .kebele(record.getKebele())
                .gender(record.getGender())
                .fullNameDisplay(record.getFullNameDisplay())
                .customFields(customFieldService.getValues(ModuleKeys.DECEASED, record.getDeceasedId()))
                .build();
    }
}
