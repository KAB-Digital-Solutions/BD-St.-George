package com.example.backend.workers.service;

import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.service.CustomFieldService;
import com.example.backend.shared.exception.ApiException;
import com.example.backend.workers.dto.WorkerRequest;
import com.example.backend.workers.dto.WorkerResponse;
import com.example.backend.workers.entity.Worker;
import com.example.backend.workers.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final CustomFieldService customFieldService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public WorkerResponse create(WorkerRequest request) {
        Worker worker = mapToEntity(new Worker(), request);
        worker = workerRepository.save(worker);
        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            customFieldService.saveValues(ModuleKeys.WORKERS, worker.getWorkerId(), request.getCustomFields());
        }
        return toResponse(worker);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public WorkerResponse update(Long id, WorkerRequest request) {
        Worker existing = workerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Worker not found"));
        Worker updated = mapToEntity(existing, request);
        updated = workerRepository.save(updated);
        if (request.getCustomFields() != null) {
            customFieldService.saveValues(ModuleKeys.WORKERS, id, request.getCustomFields());
        }
        return toResponse(updated);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public WorkerResponse get(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Worker not found"));
        return toResponse(worker);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Page<WorkerResponse> list(String q, Pageable pageable) {
        return workerRepository.search(q, pageable).map(this::toResponse);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public void deactivate(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Worker not found"));
        worker.setIsActive(false);
        workerRepository.save(worker);
    }

    @Transactional(readOnly = true)
    public void validateExists(Long workerId) {
        if (workerId != null && !workerRepository.existsById(workerId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Worker not found: " + workerId);
        }
    }

    private Worker mapToEntity(Worker worker, WorkerRequest request) {
        worker.setFullName(request.getFullName());
        worker.setPhone(request.getPhone());
        worker.setJobRole(request.getJobRole());
        worker.setGender(request.getGender());
        if (worker.getIsActive() == null) {
            worker.setIsActive(true);
        }
        return worker;
    }

    private WorkerResponse toResponse(Worker worker) {
        return WorkerResponse.builder()
                .workerId(worker.getWorkerId())
                .fullName(worker.getFullName())
                .phone(worker.getPhone())
                .jobRole(worker.getJobRole())
                .gender(worker.getGender())
                .isActive(worker.getIsActive())
                .customFields(customFieldService.getValues(ModuleKeys.WORKERS, worker.getWorkerId()))
                .build();
    }
}
