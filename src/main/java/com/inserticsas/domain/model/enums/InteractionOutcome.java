package com.inserticsas.domain.model.enums;
/**
 * Resultado de una interacción
 */
public enum InteractionOutcome {
    SUCCESSFUL("Exitosa"),
    NO_ANSWER("Sin Respuesta"),
    SCHEDULED("Agendada"),
    LOST("Perdido");

    private final String displayName;

    InteractionOutcome(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}