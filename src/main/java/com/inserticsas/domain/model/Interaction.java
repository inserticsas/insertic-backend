package com.inserticsas.domain.model;

import com.inserticsas.domain.model.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Interaction - Registro de interacciones con leads
 */
@Entity
@Table(name = "interactions")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    private Lead lead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InteractionType type;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private InteractionOutcome outcome;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
