package com.inserticsas.domain.model.enums;

/**
 * Zonas de cobertura de INSERTIC
 */
public enum Zone {
    CARTAGENA("Cartagena", "Cartagena"),
    COSTA_CARIBE("Costa Caribe", "Barranquilla, Santa Marta, Montería"),
    NACIONAL("Nacional", "Colombia");

    private final String displayName;
    private final String cities;

    Zone(String displayName, String cities) {
        this.displayName = displayName;
        this.cities = cities;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCities() {
        return cities;
    }

    public String getCityName() {
        return cities.split(",")[0].trim();
    }
}