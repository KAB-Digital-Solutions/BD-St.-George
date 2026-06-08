package com.example.backend.family.controller;

import com.example.backend.family.dto.FamilyMemberRequest;
import com.example.backend.family.dto.FamilyMemberResponse;
import com.example.backend.family.service.FamilyMemberService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/family-members")
@RequiredArgsConstructor
@Tag(name = "Family members", description = "Household relatives linked to a member record")
public class FamilyMemberController {

    private final FamilyMemberService familyMemberService;

    @Operation(summary = "Create family member", description = "Links a relative to a member; include relationship_type in customFields")
    @PostMapping
    public FamilyMemberResponse create(@Valid @RequestBody FamilyMemberRequest request) {
        return familyMemberService.create(request);
    }

    @Operation(summary = "List family members", description = "Filter by memberId and/or search name with q")
    @GetMapping
    public PageResponse<FamilyMemberResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("familyId").descending());
        return PageResponse.from(familyMemberService.list(memberId, q, pageable));
    }

    @GetMapping("/{id}")
    public FamilyMemberResponse get(@PathVariable Long id) {
        return familyMemberService.get(id);
    }

    @PutMapping("/{id}")
    public FamilyMemberResponse update(@PathVariable Long id, @Valid @RequestBody FamilyMemberRequest request) {
        return familyMemberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        familyMemberService.deactivate(id);
    }
}
