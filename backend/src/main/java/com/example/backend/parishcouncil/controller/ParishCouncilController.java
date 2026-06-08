package com.example.backend.parishcouncil.controller;

import com.example.backend.parishcouncil.dto.ParishCouncilRequest;
import com.example.backend.parishcouncil.dto.ParishCouncilResponse;
import com.example.backend.parishcouncil.service.ParishCouncilService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parish-council")
@RequiredArgsConstructor
@Tag(name = "Parish council", description = "Parish council members with optional clergy supervisor")
public class ParishCouncilController {

    private final ParishCouncilService parishCouncilService;

    @PostMapping
    public ParishCouncilResponse create(@Valid @RequestBody ParishCouncilRequest request) {
        return parishCouncilService.create(request);
    }

    @Operation(summary = "List parish council members", description = "Filter by clergyId and/or search")
    @GetMapping
    public PageResponse<ParishCouncilResponse> list(
            @RequestParam(required = false) Long clergyId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("councilId").descending());
        return PageResponse.from(parishCouncilService.list(clergyId, q, pageable));
    }

    @GetMapping("/{id}")
    public ParishCouncilResponse get(@PathVariable Long id) {
        return parishCouncilService.get(id);
    }

    @PutMapping("/{id}")
    public ParishCouncilResponse update(@PathVariable Long id, @Valid @RequestBody ParishCouncilRequest request) {
        return parishCouncilService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        parishCouncilService.deactivate(id);
    }
}
