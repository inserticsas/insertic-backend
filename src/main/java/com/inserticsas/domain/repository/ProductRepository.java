package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.Product;
import com.inserticsas.domain.model.enums.ServiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para productos
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByServiceLine(ServiceLine serviceLine);

    List<Product> findByActiveTrue();

    List<Product> findByBrand(String brand);

    // Control de inventario
    @Query("SELECT p FROM Product p WHERE p.stock <= p.minStock AND p.active = true")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.stock = 0 AND p.active = true")
    List<Product> findOutOfStockProducts();

    // Productos activos por línea de servicio
    List<Product> findByServiceLineAndActiveTrue(ServiceLine serviceLine);
}
