package com.inserticsas.domain.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Product - Producto físico del catálogo (UPS, equipos, etc)
 */
@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product extends CatalogItem {

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "min_stock")
    @Builder.Default
    private Integer minStock = 0;

    @Column(length = 100)
    private String supplier;
}