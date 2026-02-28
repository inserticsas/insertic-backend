package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.Interaction;
import com.inserticsas.domain.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para interacciones con leads
 */
@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    List<Interaction> findByLeadId(Long leadId);

    List<Interaction> findByType(InteractionType type);

    List<Interaction> findByOutcome(InteractionOutcome outcome);

    // Interacciones recientes de un lead
    @Query("SELECT i FROM Interaction i WHERE i.lead.id = ?1 ORDER BY i.createdAt DESC")
    List<Interaction> findRecentByLeadId(Long leadId);

    // Follow-ups pendientes
    @Query("SELECT i FROM Interaction i WHERE i.scheduledFor IS NOT NULL AND i.scheduledFor <= ?1")
    List<Interaction> findPendingFollowUps(LocalDateTime until);

    // Estadísticas por tipo
    Long countByType(InteractionType type);

    Long countByOutcome(InteractionOutcome outcome);

    // Interacciones en rango de fechas
    @Query("SELECT i FROM Interaction i WHERE i.createdAt BETWEEN ?1 AND ?2 ORDER BY i.createdAt DESC")
    List<Interaction> findBetweenDates(LocalDateTime start, LocalDateTime end);
}
