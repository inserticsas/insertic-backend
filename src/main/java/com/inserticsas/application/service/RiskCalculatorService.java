package com.inserticsas.application.service;

import com.inserticsas.application.dto.*;
import com.inserticsas.domain.model.*;
import com.inserticsas.domain.model.enums.*;
import com.inserticsas.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * RiskCalculatorService - Lógica de cálculo de riesgo eléctrico
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RiskCalculatorService {

    private final RiskCalculatorSessionRepository sessionRepository;
    private final LeadRepository leadRepository;
    private final LeadService leadService;

    // ─── Constantes para cálculos ────────────────────────────────────

    private static final BigDecimal WORK_HOURS_PER_MONTH = new BigDecimal("176");
    private static final BigDecimal PRODUCTIVITY_LOSS_FACTOR = new BigDecimal("0.75");
    private static final BigDecimal DATA_RECOVERY_COST = new BigDecimal("2000000");

    // ─── Métodos públicos ─────────────────────────────────────────────

    /**
     * Crear sesión de calculadora y calcular resultados
     */
    public CalculatorSessionResponse createSession(CreateCalculatorSessionRequest request) {

        // Calcular pérdida anual
        BigDecimal annualLoss = calculateAnnualLoss(
                request.getMonthlyRevenue(),
                request.getEmployees(),
                request.getAvgSalary(),
                request.getOutagesPerYear(),
                request.getHoursPerOutage(),
                request.getCriticalData()
        );

        // Determinar nivel de riesgo
        RiskLevel riskLevel = determineRiskLevel(annualLoss);

        // Recomendar UPS según número de empleados
        String recommendedUPS = recommendUPS(request.getEmployees());

        // Crear sesión
        RiskCalculatorSession session = RiskCalculatorSession.builder()
                .monthlyRevenue(request.getMonthlyRevenue())
                .employees(request.getEmployees())
                .avgSalary(request.getAvgSalary())
                .outagesPerYear(request.getOutagesPerYear())
                .hoursPerOutage(request.getHoursPerOutage())
                .criticalData(request.getCriticalData())
                .annualLoss(annualLoss)
                .recommendedUPS(recommendedUPS)
                .riskLevel(riskLevel)
                .build();

        // Configurar tipo de herramienta
        session.setToolType(ToolType.RISK_CALCULATOR);
        session.setServiceLine(ServiceLine.ENERGY);

        // Asociar con lead si existe
        if (request.getLeadId() != null) {
            Lead lead = leadRepository.findById(request.getLeadId())
                    .orElseThrow(() -> new RuntimeException("Lead no encontrado con ID: " + request.getLeadId()));

            session.setLead(lead);

            // Incrementar score del lead (usó calculadora = interés alto)
            leadService.incrementLeadScore(lead.getId(), 15);
        }

        session = sessionRepository.save(session);

        log.info("Sesión de calculadora creada: ID={}, Pérdida anual=${}, Riesgo={}",
                session.getId(), annualLoss, riskLevel);

        return mapToResponse(session);
    }

    /**
     * Obtener sesión por ID
     */
    @Transactional(readOnly = true)
    public CalculatorSessionResponse getSessionById(Long id) {
        RiskCalculatorSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con ID: " + id));

        return mapToResponse(session);
    }

    /**
     * Listar todas las sesiones
     */
    @Transactional(readOnly = true)
    public List<CalculatorSessionResponse> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Listar sesiones por nivel de riesgo
     */
    @Transactional(readOnly = true)
    public List<CalculatorSessionResponse> getByRiskLevel(RiskLevel riskLevel) {
        return sessionRepository.findByRiskLevel(riskLevel).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Contar sesiones por nivel de riesgo
     */
    @Transactional(readOnly = true)
    public Long countByRiskLevel(RiskLevel riskLevel) {
        return sessionRepository.countByRiskLevel(riskLevel);
    }

    // ─── Métodos privados de cálculo ─────────────────────────────────

    /**
     * Calcular pérdida anual total
     */
    private BigDecimal calculateAnnualLoss(
            BigDecimal monthlyRevenue,
            Integer employees,
            BigDecimal avgSalary,
            Integer outagesPerYear,
            Double hoursPerOutage,
            Boolean criticalData
    ) {
        BigDecimal hoursPerOutageBD = new BigDecimal(hoursPerOutage);
        BigDecimal outagesPerYearBD = new BigDecimal(outagesPerYear);

        // 1. Pérdida de ingresos
        BigDecimal hourlyRevenue = monthlyRevenue.divide(WORK_HOURS_PER_MONTH, 2, RoundingMode.HALF_UP);
        BigDecimal revenueLossPerOutage = hourlyRevenue.multiply(hoursPerOutageBD);
        BigDecimal totalRevenueLoss = revenueLossPerOutage.multiply(outagesPerYearBD);

        // 2. Pérdida de productividad (75% del tiempo laboral perdido)
        BigDecimal hourlyLabor = avgSalary.divide(WORK_HOURS_PER_MONTH, 2, RoundingMode.HALF_UP);
        BigDecimal productivityLossPerOutage = hourlyLabor
                .multiply(new BigDecimal(employees))
                .multiply(hoursPerOutageBD)
                .multiply(PRODUCTIVITY_LOSS_FACTOR);
        BigDecimal totalProductivityLoss = productivityLossPerOutage.multiply(outagesPerYearBD);

        // 3. Pérdida de datos (si aplica)
        BigDecimal dataLoss = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(criticalData)) {
            dataLoss = DATA_RECOVERY_COST.multiply(outagesPerYearBD);
        }

        // 4. Pérdida de clientes (estimado: 5% de facturación mensual por caída crítica)
        BigDecimal customerLoss = BigDecimal.ZERO;
        if (outagesPerYear > 4) { // Más de 4 caídas = riesgo de perder clientes
            customerLoss = monthlyRevenue
                    .multiply(new BigDecimal("0.05"))
                    .multiply(new BigDecimal(outagesPerYear - 4));
        }

        // Total
        return totalRevenueLoss
                .add(totalProductivityLoss)
                .add(dataLoss)
                .add(customerLoss)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Determinar nivel de riesgo según pérdida anual
     */
    private RiskLevel determineRiskLevel(BigDecimal annualLoss) {
        if (annualLoss.compareTo(new BigDecimal("100000000")) >= 0) {
            return RiskLevel.CRITICAL;  // >= $100M COP
        } else if (annualLoss.compareTo(new BigDecimal("30000000")) >= 0) {
            return RiskLevel.HIGH;      // >= $30M COP
        } else if (annualLoss.compareTo(new BigDecimal("10000000")) >= 0) {
            return RiskLevel.MEDIUM;    // >= $10M COP
        } else {
            return RiskLevel.LOW;       // < $10M COP
        }
    }

    /**
     * Recomendar UPS según número de empleados
     */
    private String recommendUPS(Integer employees) {
        if (employees <= 10) {
            return "UPS Oficina 1500VA";
        } else if (employees <= 50) {
            return "UPS Empresarial 6KVA";
        } else {
            return "UPS Industrial 20KVA";
        }
    }

    /**
     * Mapear sesión a DTO de respuesta
     */
    private CalculatorSessionResponse mapToResponse(RiskCalculatorSession session) {
        return CalculatorSessionResponse.builder()
                .id(session.getId())
                .leadId(session.getLead() != null ? session.getLead().getId() : null)
                .monthlyRevenue(session.getMonthlyRevenue())
                .employees(session.getEmployees())
                .avgSalary(session.getAvgSalary())
                .outagesPerYear(session.getOutagesPerYear())
                .hoursPerOutage(session.getHoursPerOutage())
                .criticalData(session.getCriticalData())
                .annualLoss(session.getAnnualLoss())
                .recommendedUPS(session.getRecommendedUPS())
                .riskLevel(session.getRiskLevel())
                .createdAt(session.getCreatedAt())
                .build();
    }
}