package com.example.backend.formengine.repository;

import com.example.backend.formengine.entity.RecordCustomField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecordCustomFieldRepository extends JpaRepository<RecordCustomField, Long> {

    List<RecordCustomField> findByModuleKeyAndRecordId(String moduleKey, Long recordId);

    Optional<RecordCustomField> findByModuleKeyAndRecordIdAndFieldKey(
            String moduleKey, Long recordId, String fieldKey);
}
