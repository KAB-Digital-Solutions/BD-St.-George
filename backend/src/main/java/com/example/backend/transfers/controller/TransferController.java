package com.example.backend.transfers.controller;

import com.example.backend.shared.dto.PageResponse;
import com.example.backend.transfers.dto.TransferRequest;
import com.example.backend.transfers.dto.TransferResponse;
import com.example.backend.transfers.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transfers", description = "Member transfers to other parishes; sets member status to TRANSFERRED")
public class TransferController {

    private final TransferService transferService;

    @Operation(summary = "Record transfer", description = "Creates transfer and marks member as TRANSFERRED")
    @PostMapping
    public TransferResponse create(@Valid @RequestBody TransferRequest request) {
        return transferService.create(request);
    }

    @GetMapping
    public PageResponse<TransferResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("transferId").descending());
        return PageResponse.from(transferService.list(memberId, q, pageable));
    }

    @GetMapping("/{id}")
    public TransferResponse get(@PathVariable Long id) {
        return transferService.get(id);
    }

    @PutMapping("/{id}")
    public TransferResponse update(@PathVariable Long id, @Valid @RequestBody TransferRequest request) {
        return transferService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        transferService.deactivate(id);
    }
}
