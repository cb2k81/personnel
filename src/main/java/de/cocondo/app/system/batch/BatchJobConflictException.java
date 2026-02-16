package de.cocondo.app.system.batch;

/**
 * Signalisiert einen fachlich/technischen Konflikt bei Batch-Operationen,
 * z. B. wenn ein Job sich noch im Übergangszustand (STOPPING) befindet.
 *
 * Wird über den GlobalExceptionHandler als HTTP 409 (Conflict) gemappt.
 */
public class BatchJobConflictException extends RuntimeException {

    public BatchJobConflictException(String message) {
        super(message);
    }
}
