package com.example.backend.contributions.service;

import com.example.backend.contributions.dto.ContributionRequest;
import com.example.backend.contributions.dto.ContributionResponse;
import com.example.backend.contributions.entity.Contribution;
import com.example.backend.contributions.repository.ContributionRepository;
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
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final MemberRepository memberRepository;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public ContributionResponse create(ContributionRequest request) {
        validateMemberExists(request.getMemberId());
        validateReceiptNoUnique(request.getReceiptNo(), null);
        Contribution record = mapToEntity(new Contribution(), request);
        if (record.getPaymentDate() == null) {
            record.setPaymentDate(java.time.LocalDate.now());
        }
        record = contributionRepository.save(record);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.CONTRIBUTIONS, record.getContribId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public ContributionResponse update(Long id, ContributionRequest request) {
        Contribution existing = contributionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contribution not found"));
        validateMemberExists(request.getMemberId());
        validateReceiptNoUnique(request.getReceiptNo(), id);
        Contribution updated = mapToEntity(existing, request);
        updated = contributionRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.CONTRIBUTIONS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public ContributionResponse get(Long id) {
        Contribution record = contributionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contribution not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<ContributionResponse> list(Long memberId, Integer ethiopianYear, String q, Pageable pageable) {
        return contributionRepository.search(memberId, ethiopianYear, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<ContributionResponse> listForMember(Long memberId) {
        validateMemberExists(memberId);
        return contributionRepository.findByMemberIdAndIsActiveTrueOrderByPaymentDateDescContribIdDesc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        Contribution record = contributionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Contribution not found"));
        record.setIsActive(false);
        contributionRepository.save(record);
    }

    private void validateMemberExists(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId);
        }
    }

    private void validateReceiptNoUnique(String receiptNo, Long excludeId) {
        boolean exists = excludeId == null
                ? contributionRepository.existsByReceiptNo(receiptNo)
                : contributionRepository.existsByReceiptNoAndContribIdNot(receiptNo, excludeId);
        if (exists) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Receipt number already exists: " + receiptNo);
        }
    }

    private Contribution mapToEntity(Contribution record, ContributionRequest request) {
        record.setMemberId(request.getMemberId());
        record.setAmount(request.getAmount());
        record.setReceiptNo(request.getReceiptNo());
        record.setEthiopianYear(request.getEthiopianYear());
        if (request.getPaymentDate() != null) {
            record.setPaymentDate(request.getPaymentDate());
        }
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private ContributionResponse toResponse(Contribution record) {
        return ContributionResponse.builder()
                .contribId(record.getContribId())
                .memberId(record.getMemberId())
                .amount(record.getAmount())
                .receiptNo(record.getReceiptNo())
                .ethiopianYear(record.getEthiopianYear())
                .paymentDate(record.getPaymentDate())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.CONTRIBUTIONS, record.getContribId()))
                .build();
    }
}
