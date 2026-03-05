package com.inserticsas.domain.model;

import com.inserticsas.domain.model.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Lead - Persona o empresa interesada en servicios de INSERTIC
 */
@Entity
@Table(name = "leads", indexes = {
        @Index(name = "idx_lead_email", columnList = "email"),
        @Index(name = "idx_lead_phone", columnList = "phone"),
        @Index(name = "idx_lead_status", columnList = "status"),
        @Index(name = "idx_lead_service_line", columnList = "service_line"),
        @Index(name = "idx_lead_privacy_accepted", columnList = "privacy_policy_accepted")
})
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_line", nullable = false, length = 20)
    private ServiceLine serviceLine;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Zone zone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeadSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private LeadStatus status = LeadStatus.NEW;

    @Column(nullable = false)
    @Builder.Default
    private Integer score = 0;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "privacy_policy_accepted", nullable = false)
    @Builder.Default
    private Boolean privacyPolicyAccepted = false;

    @Column(name = "privacy_policy_accepted_at")
    private LocalDateTime privacyPolicyAcceptedAt;

    @Column(name = "privacy_policy_version", length = 10)
    private String privacyPolicyVersion;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_contact_at")
    private LocalDateTime lastContactAt;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ToolSession> toolSessions = new ArrayList<>();

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Quote> quotes = new ArrayList<>();

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Interaction> interactions = new ArrayList<>();

    /**
     * Incrementar score del lead (máximo 100)
     */
    public void incrementScore(int points) {
        this.score = Math.min(this.score + points, 100);
    }
}