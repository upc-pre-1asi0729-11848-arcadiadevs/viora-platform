package com.arcadiadevs.viora.platform.shared.interfaces.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Lightweight liveness probe for hosting platforms (e.g. Render's Health Check
 * Path).
 *
 * <p>
 * Returns HTTP 200 whenever the application is running. It deliberately does not
 * touch the database, so a transient database outage does not flap the platform
 * health check and trigger restarts; individual endpoints still surface their own
 * failures.
 * </p>
 */
@RestController
@Tag(name = "Health", description = "Service liveness probe")
public class HealthController {

    @GetMapping("/healthz")
    @Operation(
            summary = "Liveness probe",
            description = "Returns 200 while the service is running."
    )
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
