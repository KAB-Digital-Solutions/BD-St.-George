package com.example.backend.clergy.controller;

import com.example.backend.clergy.dto.ClergyRequest;
import com.example.backend.clergy.dto.ClergyResponse;
import com.example.backend.clergy.service.ClergyService;
import com.example.backend.parishcouncil.dto.ParishCouncilResponse;
import com.example.backend.parishcouncil.service.ParishCouncilService;
import com.example.backend.shared.dto.PageResponse;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clergy")
@RequiredArgsConstructor
@Tag(name = "Clergy", description = "Priests, deacons, and spiritual fathers")
public class ClergyController {

    private final ClergyService clergyService;
    private final ParishCouncilService parishCouncilService;

    @Operation(summary = "Create clergy record")
    @PostMapping
    public ClergyResponse create(@Valid @RequestBody ClergyRequest request) {
        return clergyService.create(request);
    }

    @Operation(summary = "List clergy", description = "Search by name, phone, role, or clergy ID")
    @GetMapping
    public PageResponse<ClergyResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("clergyId").descending());
        return PageResponse.from(clergyService.list(q, pageable));
    }

    @GetMapping("/{id}")
    public ClergyResponse get(@PathVariable Long id) {
        return clergyService.get(id);
    }

    @Operation(summary = "Parish council members for clergy supervisor")
    @GetMapping("/{id}/parish-council")
    public List<ParishCouncilResponse> parishCouncil(@PathVariable Long id) {
        return parishCouncilService.listForClergy(id);
    }

    @PutMapping("/{id}")
    public ClergyResponse update(@PathVariable Long id, @Valid @RequestBody ClergyRequest request) {
        return clergyService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        clergyService.deactivate(id);
    }
}
