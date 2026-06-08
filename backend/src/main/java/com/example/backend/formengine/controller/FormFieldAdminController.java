package com.example.backend.formengine.controller;

import com.example.backend.formengine.domain.ModuleKeys;
import com.example.backend.formengine.dto.FieldDefinitionRequest;
import com.example.backend.formengine.dto.FieldDefinitionResponse;
import com.example.backend.formengine.service.FormFieldDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api/admin/form-fields")
@RequiredArgsConstructor
@Tag(name = "Form Fields (Admin)", description = "Configure dynamic form fields per module")
public class FormFieldAdminController {

    private final FormFieldDefinitionService formFieldDefinitionService;

    @Operation(summary = "List valid module keys", description = "Use one of these values in GET /api/admin/form-fields/{moduleKey}")
    @GetMapping("/modules")
    public Set<String> listModuleKeys() {
        return new TreeSet<>(ModuleKeys.all());
    }

    @Operation(summary = "List fields for a module", description = "Example: moduleKey = members")
    @GetMapping("/modules/{moduleKey}")
    public List<FieldDefinitionResponse> listByModule(
            @Parameter(description = "Module key", example = "members")
            @PathVariable String moduleKey,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return formFieldDefinitionService.listForModule(moduleKey, activeOnly);
    }

    /** @deprecated path kept for requirements compatibility — prefer GET /modules/{moduleKey} */
    @Operation(summary = "List fields (legacy path)", description = "Same as GET /modules/{moduleKey}. Pass moduleKey e.g. members")
    @GetMapping("/{moduleKey}")
    public List<FieldDefinitionResponse> list(
            @Parameter(description = "Module key", example = "members")
            @PathVariable String moduleKey,
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return formFieldDefinitionService.listForModule(moduleKey, activeOnly);
    }

    @PostMapping
    public FieldDefinitionResponse create(@Valid @RequestBody FieldDefinitionRequest request) {
        return formFieldDefinitionService.create(request);
    }

    @PutMapping("/{id}")
    public FieldDefinitionResponse update(
            @PathVariable Long id,
            @Valid @RequestBody FieldDefinitionRequest request) {
        return formFieldDefinitionService.update(id, request);
    }

    @PatchMapping("/{moduleKey}/reorder")
    public void reorder(@PathVariable String moduleKey, @RequestBody List<Long> orderedIds) {
        formFieldDefinitionService.reorder(moduleKey, orderedIds);
    }

    @PatchMapping("/{id}/deactivate")
    public FieldDefinitionResponse deactivate(@PathVariable Long id) {
        return formFieldDefinitionService.deactivate(id);
    }

    @PatchMapping("/{id}/reactivate")
    public FieldDefinitionResponse reactivate(@PathVariable Long id) {
        return formFieldDefinitionService.reactivate(id);
    }
}
