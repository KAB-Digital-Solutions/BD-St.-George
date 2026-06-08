package com.example.backend.transfers.service;

import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.members.domain.MemberStatus;
import com.example.backend.members.entity.Member;
import com.example.backend.members.repository.MemberRepository;
import com.example.backend.shared.exception.ApiException;
import com.example.backend.transfers.dto.TransferRequest;
import com.example.backend.transfers.dto.TransferResponse;
import com.example.backend.transfers.entity.Transfer;
import com.example.backend.transfers.repository.TransferRepository;
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
public class TransferService {

    private final TransferRepository transferRepository;
    private final MemberRepository memberRepository;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public TransferResponse create(TransferRequest request) {
        Member member = loadMember(request.getMemberId());
        Transfer record = mapToEntity(new Transfer(), request);
        if (record.getTransferDate() == null) {
            record.setTransferDate(java.time.LocalDate.now());
        }
        record = transferRepository.save(record);
        markMemberTransferred(member);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.TRANSFERS, record.getTransferId(), request.getCustomFields());
        }
        return toResponse(record);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public TransferResponse update(Long id, TransferRequest request) {
        Transfer existing = transferRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transfer not found"));
        loadMember(request.getMemberId());
        Transfer updated = mapToEntity(existing, request);
        updated = transferRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.TRANSFERS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public TransferResponse get(Long id) {
        Transfer record = transferRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transfer not found"));
        return toResponse(record);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<TransferResponse> list(Long memberId, String q, Pageable pageable) {
        return transferRepository.search(memberId, q, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<TransferResponse> listForMember(Long memberId) {
        loadMember(memberId);
        return transferRepository.findByMemberIdAndIsActiveTrueOrderByTransferDateDescTransferIdDesc(memberId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        Transfer record = transferRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Transfer not found"));
        record.setIsActive(false);
        transferRepository.save(record);
    }

    private Member loadMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Member not found: " + memberId));
    }

    private void markMemberTransferred(Member member) {
        member.setStatus(MemberStatus.TRANSFERRED);
        member.setIsActive(false);
        memberRepository.save(member);
    }

    private Transfer mapToEntity(Transfer record, TransferRequest request) {
        record.setMemberId(request.getMemberId());
        record.setReason(request.getReason());
        record.setRegion(request.getRegion());
        record.setDiocese(request.getDiocese());
        record.setWoreda(request.getWoreda());
        if (request.getTransferDate() != null) {
            record.setTransferDate(request.getTransferDate());
        }
        if (record.getIsActive() == null) {
            record.setIsActive(true);
        }
        return record;
    }

    private TransferResponse toResponse(Transfer record) {
        return TransferResponse.builder()
                .transferId(record.getTransferId())
                .memberId(record.getMemberId())
                .reason(record.getReason())
                .region(record.getRegion())
                .diocese(record.getDiocese())
                .woreda(record.getWoreda())
                .transferDate(record.getTransferDate())
                .isActive(record.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.TRANSFERS, record.getTransferId()))
                .build();
    }
}
