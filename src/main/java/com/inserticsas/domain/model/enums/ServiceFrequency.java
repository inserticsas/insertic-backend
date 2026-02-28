package com.inserticsas.domain.model.enums;

/**
 * Frecuencia de un servicio recurrente
 */
public enum ServiceFrequency {
    ONE_TIME("Una Vez"),
    MONTHLY("Mensual"),
    QUARTERLY("Trimestral"),
    BIANNUAL("Semestral"),
    ANNUAL("Anual");

    private final String displayName;

    ServiceFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}