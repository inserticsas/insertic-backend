package com.inserticsas.domain.model.enums;
/**
 * Tipo de interacción con el lead
 */
public enum InteractionType {
    CALL("Llamada"),
    EMAIL("Correo"),
    WHATSAPP("WhatsApp"),
    VISIT("Visita"),
    NOTE("Nota");

    private final String displayName;

    InteractionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
