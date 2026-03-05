package com.inserticsas.application.dto;

import com.inserticsas.domain.model.enums.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta con datos de un lead
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String company;
    private ServiceLine serviceLine;
    private Zone zone;
    private LeadSource source;
    private LeadStatus status;
    private Integer score;
    private Boolean privacyPolicyAccepted;
    private LocalDateTime privacyPolicyAcceptedAt;
    private String privacyPolicyVersion;
    private LocalDateTime createdAt;
    private LocalDateTime lastContactAt;
}