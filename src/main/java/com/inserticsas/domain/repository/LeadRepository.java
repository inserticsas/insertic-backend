package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.Lead;
import com.inserticsas.domain.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gestión de Leads
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    // Búsquedas básicas
    Optional<Lead> findByEmail(String email);

    Optional<Lead> findByPhone(String phone);

    // Filtros por enums
    List<Lead> findByServiceLine(ServiceLine serviceLine);

    List<Lead> findByStatus(LeadStatus status);

    List<Lead> findByZone(Zone zone);

    List<Lead> findBySource(LeadSource source);

    // Combinaciones
    List<Lead> findByServiceLineAndStatus(ServiceLine serviceLine, LeadStatus status);

    List<Lead> findByServiceLineAndZone(ServiceLine serviceLine, Zone zone);

    // Conteos (para dashboard)
    Long countByServiceLine(ServiceLine serviceLine);

    Long countByStatus(LeadStatus status);

    Long countByServiceLineAndStatus(ServiceLine serviceLine, LeadStatus status);

    // Queries por fecha
    @Query("SELECT l FROM Lead l WHERE l.createdAt >= ?1")
    List<Lead> findCreatedAfter(LocalDateTime date);

    @Query("SELECT l FROM Lead l WHERE l.createdAt BETWEEN ?1 AND ?2")
    List<Lead> findCreatedBetween(LocalDateTime start, LocalDateTime end);

    // Leads calientes (score alto)
    @Query("SELECT l FROM Lead l WHERE l.score >= ?1 ORDER BY l.score DESC")
    List<Lead> findHotLeads(Integer minScore);

    // Leads sin contactar
    @Query("SELECT l FROM Lead l WHERE l.status = 'NEW' AND l.createdAt < ?1")
    List<Lead> findUncontactedLeads(LocalDateTime olderThan);
}