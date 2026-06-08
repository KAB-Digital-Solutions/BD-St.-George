package com.example.backend.members.controller;

import com.example.backend.members.dto.MemberRequest;
import com.example.backend.members.dto.MemberResponse;
import com.example.backend.members.service.MemberService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Central member registry (fixed fields + dynamic custom fields)")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Create member", description = "Saves core fields and customFields in one request")
    @PostMapping
    public MemberResponse create(@Valid @RequestBody MemberRequest request) {
        return memberService.create(request);
    }

    @Operation(summary = "List members", description = "Search by name, phone, kebele, or member ID")
    @GetMapping
    public PageResponse<MemberResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("memberId").descending());
        return PageResponse.from(memberService.list(q, pageable));
    }

    @GetMapping("/{id}")
    public MemberResponse get(@PathVariable Long id) {
        return memberService.get(id);
    }

    @PutMapping("/{id}")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return memberService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        memberService.deactivate(id);
    }
}
