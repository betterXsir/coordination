package org.example.protocol;

import org.example.expection.CoordinationException;

public class SchemaException extends CoordinationException {
    private static final long serialVersionUID = 1L;

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(String message, Throwable t) {
        super(message, t);
    }
}
