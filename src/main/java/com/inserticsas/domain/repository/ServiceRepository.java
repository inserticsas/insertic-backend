package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.Service;
import com.inserticsas.domain.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para servicios
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    Optional<Service> findBySku(String sku);

    List<Service> findByServiceLine(ServiceLine serviceLine);

    List<Service> findByFrequency(ServiceFrequency frequency);

    List<Service> findByRecurringTrue();

    List<Service> findByActiveTrue();

    List<Service> findByServiceLineAndActiveTrue(ServiceLine serviceLine);

    List<Service> findByServiceLineAndRecurringTrue(ServiceLine serviceLine);
}
