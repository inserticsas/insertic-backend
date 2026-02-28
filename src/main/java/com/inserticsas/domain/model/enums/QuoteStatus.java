package com.inserticsas.domain.model.enums;
/**
 * Estado de la cotización
 */
public enum QuoteStatus {
    DRAFT("Borrador"),
    SENT("Enviada"),
    VIEWED("Vista por Cliente"),
    ACCEPTED("Aceptada"),
    REJECTED("Rechazada"),
    EXPIRED("Expirada");

    private final String displayName;

    QuoteStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
