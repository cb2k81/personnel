package de.cocondo.app.system.batch;

/**
 * Technische Aktion eines Runs innerhalb eines BatchJobItems.
 *
 * START  = Erststart eines neuen BatchJobItems (neue Queue-Aufbereitung etc. außerhalb dieses ADRs).
 * RESUME = Fortsetzen desselben BatchJobItems (ohne Reset der Queue).
 * RESET  = Neustart desselben BatchJobItems (Queue-Reset ist separat zu definieren/implementieren).
 *
 * Hinweis: Diese Aktion ist bewusst unabhängig vom Spring Batch "restart".
 */
public enum BatchJobRunAction {
    START,
    RESUME,
    RESET
}
