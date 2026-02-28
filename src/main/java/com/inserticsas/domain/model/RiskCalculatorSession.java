package com.inserticsas.domain.model;

import com.inserticsas.domain.model.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * RiskCalculatorSession - Sesión de calculadora de riesgo eléctrico
 */
@Entity
@Table(name = "risk_calculator_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RiskCalculatorSession extends ToolSession {

    @Column(name = "monthly_revenue", precision = 15, scale = 2)
    private BigDecimal monthlyRevenue;

    @Column(nullable = false)
    private Integer employees;

    @Column(name = "avg_salary", precision = 10, scale = 2)
    private BigDecimal avgSalary;

    @Column(name = "outages_per_year")
    private Integer outagesPerYear;

    @Column(name = "hours_per_outage")
    private Double hoursPerOutage;

    @Column(name = "critical_data")
    private Boolean criticalData;

    @Column(name = "annual_loss", precision = 15, scale = 2)
    private BigDecimal annualLoss;

    @Column(name = "recommended_ups", length = 100)
    private String recommendedUPS;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;
}