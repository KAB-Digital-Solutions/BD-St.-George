package com.example.backend.sundayschool.controller;

import com.example.backend.shared.dto.PageResponse;
import com.example.backend.sundayschool.dto.EnrollmentReportResponse;
import com.example.backend.sundayschool.dto.SundaySchoolRequest;
import com.example.backend.sundayschool.dto.SundaySchoolResponse;
import com.example.backend.sundayschool.service.SundaySchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sunday-school")
@RequiredArgsConstructor
@Tag(name = "Sunday school", description = "Sunday school students with member and clergy links")
public class SundaySchoolController {

    private final SundaySchoolService sundaySchoolService;

    @PostMapping
    public SundaySchoolResponse create(@Valid @RequestBody SundaySchoolRequest request) {
        return sundaySchoolService.create(request);
    }

    @Operation(summary = "List Sunday school students", description = "Filter by memberId, clergyId, and/or search")
    @GetMapping
    public PageResponse<SundaySchoolResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Long clergyId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("ssId").descending());
        return PageResponse.from(sundaySchoolService.list(memberId, clergyId, q, pageable));
    }

    @Operation(summary = "Enrollment report", description = "Active student count, gender breakdown, and full list")
    @GetMapping("/reports/enrollment")
    public EnrollmentReportResponse enrollmentReport() {
        return sundaySchoolService.enrollmentReport();
    }

    @GetMapping("/{id}")
    public SundaySchoolResponse get(@PathVariable Long id) {
        return sundaySchoolService.get(id);
    }

    @PutMapping("/{id}")
    public SundaySchoolResponse update(@PathVariable Long id, @Valid @RequestBody SundaySchoolRequest request) {
        return sundaySchoolService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        sundaySchoolService.deactivate(id);
    }
}
