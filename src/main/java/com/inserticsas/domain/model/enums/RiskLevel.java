package com.inserticsas.domain.model.enums;
/**
 * Nivel de riesgo calculado (para calculadora eléctrica)
 */
public enum RiskLevel {
    LOW("Bajo", "Tu empresa tiene riesgo controlado"),
    MEDIUM("Medio", "Deberías considerar protección eléctrica"),
    HIGH("Alto", "Necesitas urgente protección eléctrica"),
    CRITICAL("Crítico", "Pérdidas significativas, acción inmediata");

    private final String displayName;
    private final String message;

    RiskLevel(String displayName, String message) {
        this.displayName = displayName;
        this.message = message;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMessage() {
        return message;
    }
}