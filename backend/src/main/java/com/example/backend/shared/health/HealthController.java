package com.example.backend.shared.health;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @SecurityRequirements
    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "ok", "phase", "9-workers");
    }
}
