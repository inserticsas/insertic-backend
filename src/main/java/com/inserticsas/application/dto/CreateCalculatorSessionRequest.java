package com.inserticsas.application.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para crear sesión de calculadora de riesgo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalculatorSessionRequest {

    // leadId puede ser null (sesión anónima)
    private Long leadId;

    @NotNull(message = "La facturación mensual es obligatoria")
    @DecimalMin(value = "0", message = "La facturación debe ser positiva")
    private BigDecimal monthlyRevenue;

    @NotNull(message = "El número de empleados es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 empleado")
    private Integer employees;

    @NotNull(message = "El salario promedio es obligatorio")
    @DecimalMin(value = "0", message = "El salario debe ser positivo")
    private BigDecimal avgSalary;

    @NotNull(message = "El número de caídas al año es obligatorio")
    @Min(value = 0, message = "Las caídas no pueden ser negativas")
    private Integer outagesPerYear;

    @NotNull(message = "Las horas por caída son obligatorias")
    @DecimalMin(value = "0.5", message = "Las horas deben ser al menos 0.5")
    private Double hoursPerOutage;

    @NotNull(message = "Debe indicar si maneja datos críticos")
    private Boolean criticalData;
}