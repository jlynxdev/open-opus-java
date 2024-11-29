package dev.jlynx.openopusjava.exception;

/**
 * Thrown when an internal client operation failed.
 */
public class OpenOpusException extends RuntimeException {

    public OpenOpusException() {
    }

    public OpenOpusException(String message) {
        super(message);
    }

    public OpenOpusException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenOpusException(Throwable cause) {
        super(cause);
    }
}
