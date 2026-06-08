package com.example.backend.formengine.service;

import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.entity.RecordCustomField;
import com.example.backend.formengine.repository.FormFieldDefinitionRepository;
import com.example.backend.formengine.repository.RecordCustomFieldRepository;
import com.example.backend.shared.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomFieldService {

    private final RecordCustomFieldRepository recordCustomFieldRepository;
    private final FormFieldDefinitionRepository formFieldDefinitionRepository;

    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER', 'VIEWER')")
    public Map<String, String> getValues(String moduleKey, Long recordId) {
        validateModule(moduleKey);
        return recordCustomFieldRepository.findByModuleKeyAndRecordId(moduleKey, recordId).stream()
                .collect(LinkedHashMap::new, (m, f) -> m.put(f.getFieldKey(), f.getFieldValue()), Map::putAll);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'RECORDER')")
    public Map<String, String> saveValues(String moduleKey, Long recordId, Map<String, String> values) {
        validateModule(moduleKey);
        if (values == null || values.isEmpty()) {
            return getValues(moduleKey, recordId);
        }

        var activeDefs = formFieldDefinitionRepository
                .findByModuleKeyAndIsActiveTrueOrderBySortOrderAsc(moduleKey);

        for (var def : activeDefs) {
            if (Boolean.TRUE.equals(def.getIsRequired())) {
                String val = values.get(def.getFieldKey());
                if (val == null || val.isBlank()) {
                    throw new ApiException(HttpStatus.BAD_REQUEST,
                            "Required field missing: " + def.getFieldKey());
                }
            }
        }

        for (Map.Entry<String, String> entry : values.entrySet()) {
            RecordCustomField field = recordCustomFieldRepository
                    .findByModuleKeyAndRecordIdAndFieldKey(moduleKey, recordId, entry.getKey())
                    .orElse(RecordCustomField.builder()
                            .moduleKey(moduleKey)
                            .recordId(recordId)
                            .fieldKey(entry.getKey())
                            .build());
            field.setFieldValue(entry.getValue());
            recordCustomFieldRepository.save(field);
        }

        return getValues(moduleKey, recordId);
    }

    private void validateModule(String moduleKey) {
        if (!ModuleKeys.isValid(moduleKey)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid module key: " + moduleKey);
        }
    }
}
