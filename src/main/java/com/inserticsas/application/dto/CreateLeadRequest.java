package com.inserticsas.application.dto;

import com.inserticsas.domain.model.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para crear un nuevo lead desde el frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeadRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Teléfono inválido")
    private String phone;

    @Size(max = 100, message = "El nombre de la empresa no puede exceder 100 caracteres")
    private String company;

    @NotNull(message = "La línea de servicio es obligatoria")
    private ServiceLine serviceLine;

    @NotNull(message = "La zona es obligatoria")
    private Zone zone;

    @NotNull(message = "La fuente es obligatoria")
    private LeadSource source;

    @NotNull(message = "Debe aceptar la política de tratamiento de datos personales")
    private Boolean privacyPolicyAccepted;

    @NotBlank(message = "Se requiere la fecha de aceptación de la política")
    private String privacyPolicyAcceptedAt;

    @NotBlank(message = "Se requiere la versión de la política aceptada")
    @Size(max = 10, message = "La versión de la política no puede exceder 10 caracteres")
    private String privacyPolicyVersion;
}
