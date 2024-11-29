package dev.jlynx.openopusjava.exception;

/**
 * Thrown when the Open Opus API {@code status} object returned {@code success=false}.
 */
public class OpenOpusErrorException extends RuntimeException {

    public OpenOpusErrorException() {
    }

    public OpenOpusErrorException(String message) {
        super(message);
    }

    public OpenOpusErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
