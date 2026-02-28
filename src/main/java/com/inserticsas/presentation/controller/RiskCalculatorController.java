package com.inserticsas.presentation.controller;

import com.inserticsas.application.dto.*;
import com.inserticsas.application.service.RiskCalculatorService;
import com.inserticsas.domain.model.enums.RiskLevel;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RiskCalculatorController - API REST para calculadora de riesgo eléctrico
 */
@RestController
@RequestMapping("/api/v1/calculator/risk")
@RequiredArgsConstructor
@Slf4j
public class RiskCalculatorController {

    private final RiskCalculatorService calculatorService;

    /**
     * Crear sesión de calculadora
     * POST /api/v1/calculator/risk/sessions
     */
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<CalculatorSessionResponse>> createSession(
            @Valid @RequestBody CreateCalculatorSessionRequest request
    ) {
        log.info("Recibida solicitud de calculadora - Empleados: {}, Caídas/año: {}",
                request.getEmployees(), request.getOutagesPerYear());

        try {
            CalculatorSessionResponse response = calculatorService.createSession(request);

            log.info("Sesión creada - ID: {}, Pérdida anual: ${}, Riesgo: {}",
                    response.getId(), response.getAnnualLoss(), response.getRiskLevel());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Sesión calculada exitosamente", response));

        } catch (RuntimeException e) {
            log.error("Error al crear sesión: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener sesión por ID
     * GET /api/v1/calculator/risk/sessions/{id}
     */
    @GetMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<CalculatorSessionResponse>> getSessionById(
            @PathVariable Long id
    ) {
        log.info("Consultando sesión con ID: {}", id);

        try {
            CalculatorSessionResponse response = calculatorService.getSessionById(id);
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (RuntimeException e) {
            log.error("Sesión no encontrada: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Listar todas las sesiones
     * GET /api/v1/calculator/risk/sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<CalculatorSessionResponse>>> getAllSessions() {
        log.info("Listando todas las sesiones de calculadora");

        List<CalculatorSessionResponse> sessions = calculatorService.getAllSessions();
        return ResponseEntity.ok(
                ApiResponse.success("Se encontraron " + sessions.size() + " sesiones", sessions)
        );
    }

    /**
     * Listar sesiones por nivel de riesgo
     * GET /api/v1/calculator/risk/sessions/risk-level/{riskLevel}
     */
    @GetMapping("/sessions/risk-level/{riskLevel}")
    public ResponseEntity<ApiResponse<List<CalculatorSessionResponse>>> getByRiskLevel(
            @PathVariable RiskLevel riskLevel
    ) {
        log.info("Listando sesiones con riesgo: {}", riskLevel);

        List<CalculatorSessionResponse> sessions = calculatorService.getByRiskLevel(riskLevel);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    /**
     * Contar sesiones por nivel de riesgo
     * GET /api/v1/calculator/risk/sessions/count/risk-level/{riskLevel}
     */
    @GetMapping("/sessions/count/risk-level/{riskLevel}")
    public ResponseEntity<ApiResponse<Long>> countByRiskLevel(
            @PathVariable RiskLevel riskLevel
    ) {
        log.info("Contando sesiones con riesgo: {}", riskLevel);

        Long count = calculatorService.countByRiskLevel(riskLevel);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
