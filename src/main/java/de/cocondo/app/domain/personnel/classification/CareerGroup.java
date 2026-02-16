package de.cocondo.app.domain.personnel.classification;

/**
 * Represents the official German civil service career groups. // Repräsentiert die Laufbahngruppen des öffentlichen Dienstes
 */
public enum CareerGroup {

    MD("mD", "Mittlerer Dienst"),   // Mittlerer Dienst
    GD("gD", "Gehobener Dienst"),   // Gehobener Dienst
    HD("hD", "Höherer Dienst");     // Höherer Dienst

    private final String code; // Kurzcode (z. B. mD)
    private final String displayName; // Anzeigename

    CareerGroup(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
