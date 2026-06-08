package com.example.backend.staffministers.controller;

import com.example.backend.shared.dto.PageResponse;
import com.example.backend.staffministers.dto.StaffMinisterRequest;
import com.example.backend.staffministers.dto.StaffMinisterResponse;
import com.example.backend.staffministers.service.StaffMinisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff-ministers")
@RequiredArgsConstructor
@Tag(name = "Staff ministers", description = "Church staff ministers with education and salary custom fields")
public class StaffMinisterController {

    private final StaffMinisterService staffMinisterService;

    @Operation(summary = "Create staff minister", description = "Optional memberId links to member registry; customFields for education/salary")
    @PostMapping
    public StaffMinisterResponse create(@Valid @RequestBody StaffMinisterRequest request) {
        return staffMinisterService.create(request);
    }

    @Operation(summary = "List staff ministers", description = "Filter by memberId and/or search name, phone, employment type")
    @GetMapping
    public PageResponse<StaffMinisterResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("staffId").descending());
        return PageResponse.from(staffMinisterService.list(memberId, q, pageable));
    }

    @GetMapping("/{id}")
    public StaffMinisterResponse get(@PathVariable Long id) {
        return staffMinisterService.get(id);
    }

    @PutMapping("/{id}")
    public StaffMinisterResponse update(@PathVariable Long id, @Valid @RequestBody StaffMinisterRequest request) {
        return staffMinisterService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        staffMinisterService.deactivate(id);
    }
}
