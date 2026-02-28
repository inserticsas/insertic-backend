package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.Quote;
import com.inserticsas.domain.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para cotizaciones
 */
@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    Optional<Quote> findByQuoteNumber(String quoteNumber);

    List<Quote> findByLeadId(Long leadId);

    List<Quote> findByStatus(QuoteStatus status);

    List<Quote> findByServiceLine(ServiceLine serviceLine);

    List<Quote> findByServiceLineAndStatus(ServiceLine serviceLine, QuoteStatus status);

    // Cotizaciones por rango de fechas
    @Query("SELECT q FROM Quote q WHERE q.createdAt BETWEEN ?1 AND ?2")
    List<Quote> findCreatedBetween(LocalDateTime start, LocalDateTime end);

    // Cotizaciones expiradas
    @Query("SELECT q FROM Quote q WHERE q.validUntil < ?1 AND q.status = 'SENT'")
    List<Quote> findExpiredQuotes(LocalDateTime now);

    // Revenue pipeline
    @Query("SELECT SUM(q.total) FROM Quote q WHERE q.status IN ('SENT', 'VIEWED')")
    BigDecimal calculatePipelineValue();

    @Query("SELECT SUM(q.total) FROM Quote q WHERE q.status = 'ACCEPTED'")
    BigDecimal calculateWonValue();

    // Tasa de conversión
    @Query("SELECT COUNT(q) FROM Quote q WHERE q.status = 'ACCEPTED'")
    Long countAcceptedQuotes();

    @Query("SELECT COUNT(q) FROM Quote q WHERE q.status IN ('SENT', 'VIEWED', 'ACCEPTED', 'REJECTED')")
    Long countTotalSentQuotes();
}