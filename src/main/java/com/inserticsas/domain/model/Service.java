package com.inserticsas.domain.model;

import com.inserticsas.domain.model.enums.ServiceFrequency;
import jakarta.persistence.*;
import lombok.*;

/**
 * Service - Servicio del catálogo (mantenimiento, instalación, etc)
 */
@Entity
@Table(name = "services")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Service extends CatalogItem {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ServiceFrequency frequency;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(nullable = false)
    @Builder.Default
    private Boolean recurring = false;
}