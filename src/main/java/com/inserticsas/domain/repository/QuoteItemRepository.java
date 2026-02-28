package com.inserticsas.domain.repository;

import com.inserticsas.domain.model.QuoteItem;
import com.inserticsas.domain.model.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para items de cotización
 */
@Repository
public interface QuoteItemRepository extends JpaRepository<QuoteItem, Long> {

    List<QuoteItem> findByQuoteId(Long quoteId);

    List<QuoteItem> findByType(ItemType type);

    List<QuoteItem> findByCatalogItemId(Long catalogItemId);

    // Productos más cotizados
    @Query("SELECT qi.catalogItem.name, COUNT(qi) as count FROM QuoteItem qi " +
            "WHERE qi.catalogItem IS NOT NULL " +
            "GROUP BY qi.catalogItem.name " +
            "ORDER BY count DESC")
    List<Object[]> findMostQuotedItems();
}