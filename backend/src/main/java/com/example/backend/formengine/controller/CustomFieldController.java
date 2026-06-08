package com.example.backend.formengine.controller;

import com.example.backend.formengine.service.CustomFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    @GetMapping("/api/{moduleKey}/{recordId}/custom-fields")
    public Map<String, String> get(@PathVariable String moduleKey, @PathVariable Long recordId) {
        return customFieldService.getValues(moduleKey, recordId);
    }

    @PostMapping("/api/{moduleKey}/{recordId}/custom-fields")
    public Map<String, String> save(
            @PathVariable String moduleKey,
            @PathVariable Long recordId,
            @RequestBody Map<String, String> values) {
        return customFieldService.saveValues(moduleKey, recordId, values);
    }

    @PutMapping("/api/{moduleKey}/{recordId}/custom-fields")
    public Map<String, String> replace(
            @PathVariable String moduleKey,
            @PathVariable Long recordId,
            @RequestBody Map<String, String> values) {
        return customFieldService.saveValues(moduleKey, recordId, values);
    }
}
