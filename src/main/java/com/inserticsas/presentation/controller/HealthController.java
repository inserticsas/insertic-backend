package com.inserticsas.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * HealthController - Health checks y status de la API
 */
@RestController
@RequestMapping("/api")
@Slf4j
public class HealthController {

    /**
     * Health check simple
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "INSERTIC API");
        health.put("version", "1.0.0");
        health.put("environment", System.getProperty("SPRING_PROFILES_ACTIVE", "dev"));

        log.debug("Health check solicitado");

        return ResponseEntity.ok(health);
    }

    /**
     * Información de la API
     * GET /api/info
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "INSERTIC SAS Backend API");
        info.put("description", "Multi-service platform: Energy, Communications, Security, IT Infrastructure");
        info.put("version", "1.0.0");
        info.put("company", "INSERTIC SAS");
        info.put("location", "Cartagena, Colombia");
        info.put("documentation", "/swagger-ui.html");

        return ResponseEntity.ok(info);
    }
}