package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.ToolSession;
import com.inserticsas.domain.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para sesiones de herramientas
 */
@Repository
public interface ToolSessionRepository extends JpaRepository<ToolSession, Long> {

    List<ToolSession> findByLeadId(Long leadId);

    List<ToolSession> findByToolType(ToolType toolType);

    List<ToolSession> findByServiceLine(ServiceLine serviceLine);

    // Conteos para analytics
    Long countByToolType(ToolType toolType);

    Long countByServiceLine(ServiceLine serviceLine);

    // Sesiones recientes
    @Query("SELECT t FROM ToolSession t WHERE t.createdAt >= ?1 ORDER BY t.createdAt DESC")
    List<ToolSession> findRecentSessions(LocalDateTime since);

    // Sesiones sin lead asociado (anónimas)
    @Query("SELECT t FROM ToolSession t WHERE t.lead IS NULL")
    List<ToolSession> findAnonymousSessions();
}