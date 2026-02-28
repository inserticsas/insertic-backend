package com.inserticsas.presentation.controller;

import com.inserticsas.application.dto.*;
import com.inserticsas.application.service.LeadService;
import com.inserticsas.domain.model.enums.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LeadController - API REST para gestión de leads
 */
@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Slf4j
public class LeadController {

    private final LeadService leadService;

    /**
     * Crear un nuevo lead
     * POST /api/v1/leads
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> createLead(
            @Valid @RequestBody CreateLeadRequest request
    ) {
        log.info("Recibida solicitud para crear lead: {}", request.getEmail());

        try {
            LeadResponse response = leadService.createLead(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Lead creado exitosamente", response));

        } catch (RuntimeException e) {
            log.error("Error al crear lead: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener lead por ID
     * GET /api/v1/leads/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadById(@PathVariable Long id) {
        log.info("Consultando lead con ID: {}", id);

        try {
            LeadResponse response = leadService.getById(id);
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (RuntimeException e) {
            log.error("Lead no encontrado: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Obtener lead por email
     * GET /api/v1/leads/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<LeadResponse>> getLeadByEmail(@PathVariable String email) {
        log.info("Consultando lead con email: {}", email);

        try {
            LeadResponse response = leadService.getByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (RuntimeException e) {
            log.error("Lead no encontrado: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Listar todos los leads
     * GET /api/v1/leads
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getAllLeads() {
        log.info("Listando todos los leads");

        List<LeadResponse> leads = leadService.getAllLeads();
        return ResponseEntity.ok(
                ApiResponse.success("Se encontraron " + leads.size() + " leads", leads)
        );
    }

    /**
     * Listar leads por línea de servicio
     * GET /api/v1/leads/service-line/{serviceLine}
     */
    @GetMapping("/service-line/{serviceLine}")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getByServiceLine(
            @PathVariable ServiceLine serviceLine
    ) {
        log.info("Listando leads de la línea: {}", serviceLine);

        List<LeadResponse> leads = leadService.getByServiceLine(serviceLine);
        return ResponseEntity.ok(ApiResponse.success(leads));
    }

    /**
     * Listar leads por status
     * GET /api/v1/leads/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getByStatus(
            @PathVariable LeadStatus status
    ) {
        log.info("Listando leads con status: {}", status);

        List<LeadResponse> leads = leadService.getByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(leads));
    }

    /**
     * Listar leads creados después de una fecha
     * GET /api/v1/leads/created-after?date=2026-02-01T00:00:00
     */
    @GetMapping("/created-after")
    public ResponseEntity<ApiResponse<List<LeadResponse>>> getCreatedAfter(
            @RequestParam LocalDateTime date
    ) {
        log.info("Listando leads creados después de: {}", date);

        List<LeadResponse> leads = leadService.getCreatedAfter(date);
        return ResponseEntity.ok(ApiResponse.success(leads));
    }

    /**
     * Actualizar status de un lead
     * PATCH /api/v1/leads/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LeadResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeadStatusRequest request
    ) {
        log.info("Actualizando status del lead {} a {}", id, request.getStatus());

        try {
            LeadResponse response = leadService.updateStatus(id, request.getStatus());
            return ResponseEntity.ok(
                    ApiResponse.success("Status actualizado exitosamente", response)
            );

        } catch (RuntimeException e) {
            log.error("Error al actualizar status: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Contar leads por línea de servicio
     * GET /api/v1/leads/count/service-line/{serviceLine}
     */
    @GetMapping("/count/service-line/{serviceLine}")
    public ResponseEntity<ApiResponse<Long>> countByServiceLine(
            @PathVariable ServiceLine serviceLine
    ) {
        log.info("Contando leads de la línea: {}", serviceLine);

        Long count = leadService.countByServiceLine(serviceLine);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * Contar leads por status
     * GET /api/v1/leads/count/status/{status}
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<ApiResponse<Long>> countByStatus(
            @PathVariable LeadStatus status
    ) {
        log.info("Contando leads con status: {}", status);

        Long count = leadService.countByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
