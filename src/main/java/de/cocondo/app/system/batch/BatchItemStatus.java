package de.cocondo.app.system.batch;

public enum BatchItemStatus {
    CREATED,    // JobItem wurde angelegt, aber Job noch nicht gestartet
    RUNNING,    // Job ist aktiv in Ausführung
    COMPLETED,  // Job wurde erfolgreich abgeschlossen
    FAILED,     // Job ist fehlgeschlagen
    STOPPED,    // Job wurde gestoppt (z.B. manuell)
    ABANDONED,  // Job wurde aufgegeben (Spring Batch spezifisch)
    UNKNOWN     // Unbekannter Status
}
