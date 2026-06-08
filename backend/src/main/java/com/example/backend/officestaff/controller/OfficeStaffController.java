package com.example.backend.officestaff.controller;

import com.example.backend.officestaff.dto.OfficeStaffRequest;
import com.example.backend.officestaff.dto.OfficeStaffResponse;
import com.example.backend.officestaff.service.OfficeStaffService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/office-staff")
@RequiredArgsConstructor
@Tag(name = "Office staff", description = "Parish office employees with optional member and clergy links")
public class OfficeStaffController {

    private final OfficeStaffService officeStaffService;

    @Operation(summary = "Create office staff record")
    @PostMapping
    public OfficeStaffResponse create(@Valid @RequestBody OfficeStaffRequest request) {
        return officeStaffService.create(request);
    }

    @Operation(summary = "List office staff", description = "Filter by memberId, clergyId, and/or search name, phone, position")
    @GetMapping
    public PageResponse<OfficeStaffResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long clergyId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("officeId").descending());
        return PageResponse.from(officeStaffService.list(memberId, clergyId, q, pageable));
    }

    @GetMapping("/{id}")
    public OfficeStaffResponse get(@PathVariable Long id) {
        return officeStaffService.get(id);
    }

    @PutMapping("/{id}")
    public OfficeStaffResponse update(@PathVariable Long id, @Valid @RequestBody OfficeStaffRequest request) {
        return officeStaffService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        officeStaffService.deactivate(id);
    }
}
