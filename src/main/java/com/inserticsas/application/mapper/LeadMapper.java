package com.inserticsas.application.mapper;

import com.inserticsas.application.dto.CreateLeadRequest;
import com.inserticsas.application.dto.LeadResponse;
import com.inserticsas.domain.model.Lead;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mapper para Lead
 */
@Component
public class LeadMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Convierte CreateLeadRequest a Lead entity
     */
    public Lead toEntity(CreateLeadRequest dto) {
        return Lead.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .company(dto.getCompany())
                .serviceLine(dto.getServiceLine())
                .zone(dto.getZone())
                .source(dto.getSource())
                .privacyPolicyAccepted(dto.getPrivacyPolicyAccepted())
                .privacyPolicyAcceptedAt(parseDateTime(dto.getPrivacyPolicyAcceptedAt()))
                .privacyPolicyVersion(dto.getPrivacyPolicyVersion())
                .build();
    }

    /**
     * Convierte Lead entity a LeadResponse
     */
    public LeadResponse toResponse(Lead entity) {
        return LeadResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .company(entity.getCompany())
                .serviceLine(entity.getServiceLine())
                .zone(entity.getZone())
                .source(entity.getSource())
                .status(entity.getStatus())
                .score(entity.getScore())
                .privacyPolicyAccepted(entity.getPrivacyPolicyAccepted())
                .privacyPolicyAcceptedAt(entity.getPrivacyPolicyAcceptedAt())
                .privacyPolicyVersion(entity.getPrivacyPolicyVersion())
                .createdAt(entity.getCreatedAt())
                .lastContactAt(entity.getLastContactAt())
                .build();
    }

    /**
     * Parsea ISO string a LocalDateTime
     */
    private LocalDateTime parseDateTime(String isoString) {
        if (isoString == null || isoString.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(isoString, ISO_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}