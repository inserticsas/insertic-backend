package com.inserticsas.application.dto;

import com.inserticsas.domain.model.enums.RiskLevel;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con resultado de calculadora
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculatorSessionResponse {

    private Long id;
    private Long leadId;

    // Inputs
    private BigDecimal monthlyRevenue;
    private Integer employees;
    private BigDecimal avgSalary;
    private Integer outagesPerYear;
    private Double hoursPerOutage;
    private Boolean criticalData;

    // Resultados calculados
    private BigDecimal annualLoss;
    private String recommendedUPS;
    private RiskLevel riskLevel;

    private LocalDateTime createdAt;
}