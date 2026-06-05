package com.example.backend.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Phase 0 smoke test — remove or replace when real APIs exist.
 */
@RestController
public class SetupHealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "phase", "1-auth");
    }
}
