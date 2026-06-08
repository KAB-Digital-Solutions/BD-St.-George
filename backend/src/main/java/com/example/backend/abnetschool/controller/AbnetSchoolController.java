package com.example.backend.abnetschool.controller;

import com.example.backend.abnetschool.dto.AbnetEnrollmentReportResponse;
import com.example.backend.abnetschool.dto.AbnetSchoolRequest;
import com.example.backend.abnetschool.dto.AbnetSchoolResponse;
import com.example.backend.abnetschool.service.AbnetSchoolService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/abnet-school")
@RequiredArgsConstructor
@Tag(name = "Abnet school", description = "Abnet school students with member and clergy links")
public class AbnetSchoolController {

    private final AbnetSchoolService abnetSchoolService;

    @PostMapping
    public AbnetSchoolResponse create(@Valid @RequestBody AbnetSchoolRequest request) {
        return abnetSchoolService.create(request);
    }

    @Operation(summary = "List Abnet school students", description = "Filter by memberId, clergyId, and/or search")
    @GetMapping
    public PageResponse<AbnetSchoolResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long clergyId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("abnetId").descending());
        return PageResponse.from(abnetSchoolService.list(memberId, clergyId, q, pageable));
    }

    @Operation(summary = "Enrollment report", description = "Active student count, gender breakdown, and full list")
    @GetMapping("/reports/enrollment")
    public AbnetEnrollmentReportResponse enrollmentReport() {
        return abnetSchoolService.enrollmentReport();
    }

    @GetMapping("/{id}")
    public AbnetSchoolResponse get(@PathVariable Long id) {
        return abnetSchoolService.get(id);
    }

    @PutMapping("/{id}")
    public AbnetSchoolResponse update(@PathVariable Long id, @Valid @RequestBody AbnetSchoolRequest request) {
        return abnetSchoolService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        abnetSchoolService.deactivate(id);
    }
}
