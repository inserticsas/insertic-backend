package com.inserticsas.domain.model.enums;

/**
 * Estado del lead en el funnel de ventas
 */
public enum LeadStatus {
    NEW("Nuevo", 0),
    CONTACTED("Contactado", 1),
    QUALIFIED("Calificado", 2),
    QUOTED("Cotizado", 3),
    NEGOTIATING("En Negociación", 4),
    WON("Ganado", 5),
    LOST("Perdido", -1);

    private final String displayName;
    private final int order;

    LeadStatus(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }
}
