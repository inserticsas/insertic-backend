package com.inserticsas.domain.model.enums;
/**
 * Fuente de origen del lead
 */
public enum LeadSource {
    CALCULATOR("Calculadora de Riesgo"),
    CONTACT_FORM("Formulario de Contacto"),
    CHATBOT("Chatbot WhatsApp"),
    PHONE("Llamada Telefónica"),
    EMAIL("Correo Electrónico"),
    REFERRAL("Referido"),
    WEBSITE("Sitio Web"),
    SOCIAL_MEDIA("Redes Sociales");

    private final String displayName;

    LeadSource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}