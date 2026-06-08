package com.example.backend.emergencycontacts.controller;

import com.example.backend.emergencycontacts.dto.EmergencyContactRequest;
import com.example.backend.emergencycontacts.dto.EmergencyContactResponse;
import com.example.backend.emergencycontacts.service.EmergencyContactService;
import com.example.backend.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emergency-contacts")
@RequiredArgsConstructor
@Tag(name = "Emergency contacts", description = "Emergency contacts linked to workers or office staff")
public class EmergencyContactController {

    private final EmergencyContactService emergencyContactService;

    @Operation(summary = "Create emergency contact", description = "Requires workerId and/or officeId")
    @PostMapping
    public EmergencyContactResponse create(@Valid @RequestBody EmergencyContactRequest request) {
        return emergencyContactService.create(request);
    }

    @Operation(summary = "List emergency contacts", description = "Filter by workerId, officeId, and/or search")
    @GetMapping
    public PageResponse<EmergencyContactResponse> list(
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) Long officeId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("contactId").descending());
        return PageResponse.from(emergencyContactService.list(workerId, officeId, q, pageable));
    }

    @GetMapping("/{id}")
    public EmergencyContactResponse get(@PathVariable Long id) {
        return emergencyContactService.get(id);
    }

    @PutMapping("/{id}")
    public EmergencyContactResponse update(@PathVariable Long id, @Valid @RequestBody EmergencyContactRequest request) {
        return emergencyContactService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        emergencyContactService.deactivate(id);
    }
}
