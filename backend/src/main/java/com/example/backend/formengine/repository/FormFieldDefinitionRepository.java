package com.example.backend.formengine.repository;

import com.example.backend.formengine.entity.FormFieldDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormFieldDefinitionRepository extends JpaRepository<FormFieldDefinition, Long> {

    List<FormFieldDefinition> findByModuleKeyOrderBySortOrderAsc(String moduleKey);

    List<FormFieldDefinition> findByModuleKeyAndIsActiveTrueOrderBySortOrderAsc(String moduleKey);

    Optional<FormFieldDefinition> findByModuleKeyAndFieldKey(String moduleKey, String fieldKey);
}
