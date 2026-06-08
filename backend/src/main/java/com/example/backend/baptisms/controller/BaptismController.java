package com.example.backend.baptisms.controller;

import com.example.backend.baptisms.dto.BaptismRequest;
import com.example.backend.baptisms.dto.BaptismResponse;
import com.example.backend.baptisms.service.BaptismService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/baptisms")
@RequiredArgsConstructor
@Tag(name = "Baptisms", description = "Child baptism records linked to members and clergy")
public class BaptismController {

    private final BaptismService baptismService;

    @Operation(summary = "Create baptism record")
    @PostMapping
    public BaptismResponse create(@Valid @RequestBody BaptismRequest request) {
        return baptismService.create(request);
    }

    @Operation(summary = "List baptism records", description = "Filter by memberId and/or search child name or church")
    @GetMapping
    public PageResponse<BaptismResponse> list(
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("baptismId").descending());
        return PageResponse.from(baptismService.list(memberId, q, pageable));
    }

    @GetMapping("/{id}")
    public BaptismResponse get(@PathVariable Long id) {
        return baptismService.get(id);
    }

    @PutMapping("/{id}")
    public BaptismResponse update(@PathVariable Long id, @Valid @RequestBody BaptismRequest request) {
        return baptismService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        baptismService.deactivate(id);
    }
}
