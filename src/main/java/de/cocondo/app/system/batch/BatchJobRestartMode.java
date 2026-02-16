package de.cocondo.app.system.batch;

/**
 * Phase 5.1 – Restart-/Resume-Steuerung beim Job-Start.
 *
 * RESUME (Default):
 * - vorhandene Progress-/Queue-Daten werden weiterverwendet (Wiederaufnahme).
 *
 * RESET:
 * - Progress-/Queue-Daten werden ignoriert/neu initialisiert (Neustart).
 *
 * Die tatsächliche fachliche Umsetzung folgt in Phase 5.2. In Phase 5.1 wird der Parameter
 * als API-Vertrag eingeführt, validiert und normalisiert.
 */
public enum BatchJobRestartMode {

    RESUME,
    RESET;

    public static BatchJobRestartMode fromStringOrDefault(String value, BatchJobRestartMode defaultMode) {
        if (value == null || value.isBlank()) {
            return defaultMode;
        }
        String v = value.trim().toUpperCase();
        try {
            return BatchJobRestartMode.valueOf(v);
        } catch (IllegalArgumentException ex) {
            // Fail-safe: Default verwenden, um bestehende Clients nicht zu brechen.
            return defaultMode;
        }
    }
}
