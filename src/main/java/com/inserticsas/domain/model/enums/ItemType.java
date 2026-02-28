package com.inserticsas.domain.model.enums;
/**
 * Tipo de item en cotización o catálogo
 */
public enum ItemType {
    PRODUCT("Producto"),
    SERVICE("Servicio"),
    LABOR("Mano de Obra"),
    OTHER("Otro");

    private final String displayName;

    ItemType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}