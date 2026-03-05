package com.inserticsas.application.service;

import com.inserticsas.application.dto.*;
import com.inserticsas.application.mapper.LeadMapper;
import com.inserticsas.domain.model.Lead;
import com.inserticsas.domain.model.enums.*;
import com.inserticsas.domain.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * LeadService - Gestión de leads (contactos interesados)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    /**
     * Crear un nuevo lead
     */
    public LeadResponse createLead(CreateLeadRequest request) {

        // Valida el aceptacion de la politica
        if (request.getPrivacyPolicyAccepted() == null || !request.getPrivacyPolicyAccepted()) {
            log.error("Intento de crear lead sin aceptación de política de privacidad");
            throw new IllegalArgumentException(
                    "Debe aceptar la política de tratamiento de datos personales (Ley 1581 de 2012)"
            );
        }

        // Validar si ya existe

        if (leadRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Ya existe un lead con este email {}", request.getEmail());

            // Obtener lead existente y actualizar último contacto
            Lead existingLead = leadRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Lead no encontrado"));

            existingLead.setLastContactAt(LocalDateTime.now());
            existingLead.incrementScore(5); // Bonus por re-contacto

            Lead updated = leadRepository.save(existingLead);
            return leadMapper.toResponse(updated);
        }

        Lead lead = leadMapper.toEntity(request);

        //Asegurar que campos de consentimiento estén presentes
        if (lead.getPrivacyPolicyAcceptedAt() == null) {
            lead.setPrivacyPolicyAcceptedAt(LocalDateTime.now());
        }

        // Guardar
        Lead savedLead = leadRepository.save(lead);

        log.info("Lead creado exitosamente: ID={}, Email={}, Consentimiento={}",
                savedLead.getId(),
                savedLead.getEmail(),
                savedLead.getPrivacyPolicyVersion());

        return leadMapper.toResponse(savedLead);
    }

    /**
     * Obtener lead por ID
     */
    @Transactional(readOnly = true)
    public LeadResponse getById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado con ID: " + id));

        return mapToResponse(lead);
    }

    /**
     * Obtener lead por email
     */
    @Transactional(readOnly = true)
    public LeadResponse getByEmail(String email) {
        Lead lead = leadRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado con email: " + email));

        return mapToResponse(lead);
    }

    /**
     * Listar todos los leads
     */
    @Transactional(readOnly = true)
    public List<LeadResponse> getAllLeads() {
        return leadRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Listar leads por línea de servicio
     */
    @Transactional(readOnly = true)
    public List<LeadResponse> getByServiceLine(ServiceLine serviceLine) {
        return leadRepository.findByServiceLine(serviceLine).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Listar leads por status
     */
    @Transactional(readOnly = true)
    public List<LeadResponse> getByStatus(LeadStatus status) {
        return leadRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Listar leads creados después de una fecha
     */
    @Transactional(readOnly = true)
    public List<LeadResponse> getCreatedAfter(LocalDateTime date) {
        return leadRepository.findCreatedAfter(date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar status de un lead
     */
    public LeadResponse updateStatus(Long id, LeadStatus newStatus) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado con ID: " + id));

        LeadStatus oldStatus = lead.getStatus();
        lead.setStatus(newStatus);
        lead.setLastContactAt(LocalDateTime.now());

        // Incrementar score si avanzó en el funnel
        if (newStatus.getOrder() > oldStatus.getOrder()) {
            lead.incrementScore(10);
        }

        lead = leadRepository.save(lead);

        log.info("Lead {} actualizado: {} → {}", id, oldStatus, newStatus);

        return mapToResponse(lead);
    }

    /**
     * Incrementar score de un lead
     */
    public void incrementLeadScore(Long leadId, int points) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado con ID: " + leadId));

        lead.incrementScore(points);
        leadRepository.save(lead);

        log.debug("Score del lead {} incrementado en {} puntos. Score actual: {}",
                leadId, points, lead.getScore());
    }

    /**
     * Contar leads por línea de servicio
     */
    @Transactional(readOnly = true)
    public Long countByServiceLine(ServiceLine serviceLine) {
        return leadRepository.countByServiceLine(serviceLine);
    }

    /**
     * Contar leads por status
     */
    @Transactional(readOnly = true)
    public Long countByStatus(LeadStatus status) {
        return leadRepository.countByStatus(status);
    }

    // ─── Métodos privados ─────────────────────────────────────────────

    /**
     * Calcular score inicial según fuente
     */
    private int calculateInitialScore(LeadSource source) {
        return switch (source) {
            case CALCULATOR -> 30;      // Alta intención (usó calculadora)
            case CHATBOT -> 25;         // Interacción proactiva
            case CONTACT_FORM -> 20;    // Completó formulario
            case REFERRAL -> 20;        // Referido (calidad alta)
            case PHONE -> 15;           // Llamó directamente
            case EMAIL -> 10;           // Bajo esfuerzo
            case WEBSITE -> 5;          // Solo navegó
            case SOCIAL_MEDIA -> 5;     // Engagement bajo
        };
    }

    /**
     * Mapear Lead a LeadResponse
     */
    private LeadResponse mapToResponse(Lead lead) {
        return LeadResponse.builder()
                .id(lead.getId())
                .name(lead.getName())
                .email(lead.getEmail())
                .phone(lead.getPhone())
                .company(lead.getCompany())
                .serviceLine(lead.getServiceLine())
                .zone(lead.getZone())
                .source(lead.getSource())
                .status(lead.getStatus())
                .score(lead.getScore())
                .createdAt(lead.getCreatedAt())
                .lastContactAt(lead.getLastContactAt())
                .build();
    }
}
