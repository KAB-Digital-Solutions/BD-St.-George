package com.example.backend.workers.controller;

import com.example.backend.emergencycontacts.dto.EmergencyContactResponse;
import com.example.backend.emergencycontacts.service.EmergencyContactService;
import com.example.backend.shared.dto.PageResponse;
import com.example.backend.workers.dto.WorkerRequest;
import com.example.backend.workers.dto.WorkerResponse;
import com.example.backend.workers.service.WorkerService;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workers")
@RequiredArgsConstructor
@Tag(name = "Workers", description = "Church workers — maintenance, grounds, and support staff")
public class WorkerController {

    private final WorkerService workerService;
    private final EmergencyContactService emergencyContactService;

    @Operation(summary = "Create worker record")
    @PostMapping
    public WorkerResponse create(@Valid @RequestBody WorkerRequest request) {
        return workerService.create(request);
    }

    @Operation(summary = "List workers", description = "Search by name, phone, job role, or worker ID")
    @GetMapping
    public PageResponse<WorkerResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size, Sort.by("workerId").descending());
        return PageResponse.from(workerService.list(q, pageable));
    }

    @GetMapping("/{id}")
    public WorkerResponse get(@PathVariable Long id) {
        return workerService.get(id);
    }

    @Operation(summary = "Emergency contacts for worker")
    @GetMapping("/{id}/emergency-contacts")
    public List<EmergencyContactResponse> emergencyContacts(@PathVariable Long id) {
        return emergencyContactService.listForWorker(id);
    }

    @PutMapping("/{id}")
    public WorkerResponse update(@PathVariable Long id, @Valid @RequestBody WorkerRequest request) {
        return workerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        workerService.deactivate(id);
    }
}
