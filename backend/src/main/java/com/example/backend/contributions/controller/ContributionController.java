package com.example.backend.contributions.controller;

import com.example.backend.contributions.dto.ContributionRequest;
import com.example.backend.contributions.dto.ContributionResponse;
import com.example.backend.contributions.service.ContributionService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contributions")
@RequiredArgsConstructor
@Tag(name = "Contributions", description = "Member contribution payments with unique receipt numbers")
public class ContributionController {

    private final ContributionService contributionService;

    @PostMapping
    public ContributionResponse create(@Valid @RequestBody ContributionRequest request) {
        return contributionService.create(request);
    }

    @Operation(summary = "List contributions", description = "Filter by memberId, ethiopianYear, and/or receipt search")
    @GetMapping
    public PageResponse<ContributionResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Integer ethiopianYear,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("contribId").descending());
        return PageResponse.from(contributionService.list(memberId, ethiopianYear, q, pageable));
    }

    @GetMapping("/{id}")
    public ContributionResponse get(@PathVariable Long id) {
        return contributionService.get(id);
    }

    @PutMapping("/{id}")
    public ContributionResponse update(@PathVariable Long id, @Valid @RequestBody ContributionRequest request) {
        return contributionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        contributionService.deactivate(id);
    }
}
