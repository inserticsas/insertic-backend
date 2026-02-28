package com.inserticsas.domain.model.enums;

/**
 * Líneas de servicio de INSERTIC SAS
 */
public enum ServiceLine {
    ENERGY("Energía & UPS"),
    COMMUNICATION("Comunicaciones"),
    SECURITY("Seguridad Electrónica"),
    INFRASTRUCTURE("Infraestructura IT");

    private final String displayName;

    ServiceLine(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
