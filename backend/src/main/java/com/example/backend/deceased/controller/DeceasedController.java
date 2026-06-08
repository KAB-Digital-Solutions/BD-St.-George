package com.example.backend.deceased.controller;

import com.example.backend.deceased.dto.DeceasedRequest;
import com.example.backend.deceased.dto.DeceasedResponse;
import com.example.backend.deceased.service.DeceasedService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deceased")
@RequiredArgsConstructor
@Tag(name = "Deceased", description = "Immutable deceased member archive (POST + read only)")
public class DeceasedController {

    private final DeceasedService deceasedService;

    @Operation(summary = "Archive deceased member", description = "Creates immutable record and sets member status to DECEASED")
    @PostMapping
    public DeceasedResponse create(@Valid @RequestBody DeceasedRequest request) {
        return deceasedService.create(request);
    }

    @GetMapping
    public PageResponse<DeceasedResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("deceasedId").descending());
        return PageResponse.from(deceasedService.list(q, pageable));
    }

    @GetMapping("/{id}")
    public DeceasedResponse get(@PathVariable Long id) {
        return deceasedService.get(id);
    }
}
