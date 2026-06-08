package com.example.backend.formengine.service;

import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.dto.FieldDefinitionRequest;
import com.example.backend.formengine.dto.FieldDefinitionResponse;
import com.example.backend.formengine.entity.FormFieldDefinition;
import com.example.backend.formengine.repository.FormFieldDefinitionRepository;
import com.example.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormFieldDefinitionService {

    private final FormFieldDefinitionRepository repository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public List<FieldDefinitionResponse> listForModule(String moduleKey, boolean activeOnly) {
        validateModule(moduleKey);
        List<FormFieldDefinition> fields = activeOnly
                ? repository.findByModuleKeyAndIsActiveTrueOrderBySortOrderAsc(moduleKey)
                : repository.findByModuleKeyOrderBySortOrderAsc(moduleKey);
        return fields.stream().map(this::toResponse).toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public FieldDefinitionResponse create(FieldDefinitionRequest request) {
        validateModule(request.getModuleKey());
        if (repository.findByModuleKeyAndFieldKey(request.getModuleKey(), request.getFieldKey()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Field key already exists for this module");
        }
        if (request.getFieldType() == com.example.backend.formengine.domain.FieldType.DROPDOWN
                && (request.getDropdownOpts() == null || request.getDropdownOpts().isEmpty())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "DROPDOWN fields require at least one option");
        }
        FormFieldDefinition entity = map(request, new FormFieldDefinition());
        entity.setIsSystemCore(false);
        return toResponse(repository.save(entity));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public FieldDefinitionResponse update(Long id, FieldDefinitionRequest request) {
        FormFieldDefinition existing = repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Field definition not found"));
        if (Boolean.TRUE.equals(existing.getIsSystemCore())
                && request.getFieldKey() != null
                && !request.getFieldKey().equals(existing.getFieldKey())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "System core field keys cannot be changed");
        }
        FormFieldDefinition updated = map(request, existing);
        return toResponse(repository.save(updated));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void reorder(String moduleKey, List<Long> orderedIds) {
        validateModule(moduleKey);
        int order = 1;
        for (Long id : orderedIds) {
            FormFieldDefinition field = repository.findById(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Field not found: " + id));
            if (!moduleKey.equals(field.getModuleKey())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Field does not belong to module: " + moduleKey);
            }
            field.setSortOrder(order++);
            repository.save(field);
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public FieldDefinitionResponse deactivate(Long id) {
        FormFieldDefinition field = repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Field definition not found"));
        if (Boolean.TRUE.equals(field.getIsSystemCore())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "System core fields cannot be deactivated");
        }
        field.setIsActive(false);
        return toResponse(repository.save(field));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public FieldDefinitionResponse reactivate(Long id) {
        FormFieldDefinition field = repository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Field definition not found"));
        field.setIsActive(true);
        return toResponse(repository.save(field));
    }

    private FormFieldDefinition map(FieldDefinitionRequest dto, FormFieldDefinition entity) {
        entity.setModuleKey(dto.getModuleKey());
        entity.setFieldKey(dto.getFieldKey());
        entity.setLabelAm(dto.getLabelAm());
        entity.setLabelEn(dto.getLabelEn());
        entity.setFieldType(dto.getFieldType());
        entity.setDropdownOpts(dto.getDropdownOpts());
        entity.setValidationRules(dto.getValidationRules());
        if (dto.getIsRequired() != null) {
            entity.setIsRequired(dto.getIsRequired());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        } else if (entity.getSortOrder() == null) {
            entity.setSortOrder(999);
        }
        return entity;
    }

    private FieldDefinitionResponse toResponse(FormFieldDefinition f) {
        return FieldDefinitionResponse.builder()
                .id(f.getId())
                .moduleKey(f.getModuleKey())
                .fieldKey(f.getFieldKey())
                .labelAm(f.getLabelAm())
                .labelEn(f.getLabelEn())
                .fieldType(f.getFieldType())
                .dropdownOpts(f.getDropdownOpts())
                .validationRules(f.getValidationRules())
                .isRequired(f.getIsRequired())
                .isActive(f.getIsActive())
                .isSystemCore(f.getIsSystemCore())
                .sortOrder(f.getSortOrder())
                .build();
    }

    private void validateModule(String moduleKey) {
        if (!ModuleKeys.isValid(moduleKey)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid module key: " + moduleKey);
        }
    }
}
