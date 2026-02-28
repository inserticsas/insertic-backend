package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.CatalogItem;
import com.inserticsas.domain.model.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para items del catálogo
 */
@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {

    Optional<CatalogItem> findBySku(String sku);

    List<CatalogItem> findByServiceLine(ServiceLine serviceLine);

    List<CatalogItem> findByType(ItemType type);

    List<CatalogItem> findByActiveTrue();

    List<CatalogItem> findByServiceLineAndType(ServiceLine serviceLine, ItemType type);

    List<CatalogItem> findByServiceLineAndActiveTrue(ServiceLine serviceLine);
}
