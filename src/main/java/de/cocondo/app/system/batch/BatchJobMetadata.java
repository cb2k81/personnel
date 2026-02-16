package de.cocondo.app.system.batch;

/**
 * Interface zur Definition von anwendungsspezifischen Metadaten für Batch-Jobs.
 * Jede Spring @Configuration-Klasse, die einen Batch-Job definiert, sollte dieses Interface implementieren.
 * Dies ermöglicht eine typsichere Abfrage von Job-Eigenschaften und eine automatische Initialisierung
 * der BatchJobDefinition in der Datenbank.
 */
public interface BatchJobMetadata {

    /**
     * Gibt den eindeutigen Namen des Spring Batch Jobs zurück.
     * Dieser Name muss dem Bean-Namen des Jobs entsprechen und wird als
     * Schlüssel zur BatchJobDefinition verwendet.
     *
     * @return Der technische Name des Jobs.
     */
    String getJobName();

    /**
     * Gibt eine menschenlesbare Beschreibung des Jobs zurück.
     *
     * @return Die Beschreibung des Jobs.
     */
    String getJobDescription();

    /**
     * Gibt an, ob dieser Job als Single-Instance (nur eine Ausführung gleichzeitig) laufen darf.
     *
     * @return true, wenn nur eine Instanz erlaubt ist, sonst false.
     */
    boolean isSingleInstance();

    /**
     * Gibt die maximale Anzahl gleichzeitig erlaubter Ausführungen zurück,
     * wenn isSingleInstance() false ist.
     *
     * @return Die maximale Anzahl paralleler Ausführungen oder null/0 für unbegrenzt.
     */
    Integer getMaxConcurrentExecutions();

    /**
     * Soll der Job automatisch beim Start getriggert werden?
     */
    default boolean isAutoStart() {
        return false;
    }

    /**
     * Gibt den Benutzername an, unter dem der Job ausgeführt werden soll.
     *
     * @return Benutzername für die Kontextinitialisierung.
     */
    default String getRunAsUsername() {
        return "system";
    }
}
