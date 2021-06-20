package org.example.expection;

public class CoordinationException extends RuntimeException {
    public CoordinationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoordinationException(Throwable cause) {
        super(cause);
    }

    public CoordinationException(String message) {
        super(message);
    }

    public CoordinationException() {
        super();
    }
}
