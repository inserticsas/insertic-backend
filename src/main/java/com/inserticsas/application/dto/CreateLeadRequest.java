package com.inserticsas.application.dto;

import com.inserticsas.domain.model.enums.*;
import jakarta.validation.constraints.*;
import lombok.*;

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
}
