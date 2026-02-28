package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.RiskCalculatorSession;
import com.inserticsas.domain.model.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository para sesiones de calculadora de riesgo
 */
@Repository
public interface RiskCalculatorSessionRepository extends JpaRepository<RiskCalculatorSession, Long> {

    List<RiskCalculatorSession> findByRiskLevel(RiskLevel riskLevel);

    List<RiskCalculatorSession> findByLeadId(Long leadId);

    // Conteos por nivel de riesgo
    Long countByRiskLevel(RiskLevel riskLevel);

    // Estadísticas
    @Query("SELECT AVG(r.annualLoss) FROM RiskCalculatorSession r")
    BigDecimal findAverageAnnualLoss();

    @Query("SELECT r FROM RiskCalculatorSession r WHERE r.annualLoss >= ?1 ORDER BY r.annualLoss DESC")
    List<RiskCalculatorSession> findHighValueSessions(BigDecimal minLoss);

    // Top oportunidades
    @Query("SELECT r FROM RiskCalculatorSession r WHERE r.lead IS NOT NULL ORDER BY r.annualLoss DESC")
    List<RiskCalculatorSession> findTopOpportunities();
}