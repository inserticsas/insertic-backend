package com.inserticsas.domain.model.enums;
/**
 * Tipos de herramientas/calculadoras disponibles
 */
public enum ToolType {
    RISK_CALCULATOR("Calculadora de Riesgo Eléctrico", ServiceLine.ENERGY),
    CCTV_CALCULATOR("Calculadora de CCTV", ServiceLine.SECURITY),
    VOIP_ESTIMATOR("Estimador de VoIP", ServiceLine.COMMUNICATION),
    NETWORK_PLANNER("Planificador de Redes", ServiceLine.COMMUNICATION),
    SERVER_SIZING("Dimensionamiento de Servidores", ServiceLine.INFRASTRUCTURE);

    private final String displayName;
    private final ServiceLine serviceLine;

    ToolType(String displayName, ServiceLine serviceLine) {
        this.displayName = displayName;
        this.serviceLine = serviceLine;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ServiceLine getServiceLine() {
        return serviceLine;
    }
}