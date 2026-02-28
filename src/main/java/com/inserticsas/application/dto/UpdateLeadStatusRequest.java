package com.inserticsas.application.dto;

import com.inserticsas.domain.model.enums.LeadStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para actualizar el status de un lead
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLeadStatusRequest {

    @NotNull(message = "El nuevo status es obligatorio")
    private LeadStatus status;
}