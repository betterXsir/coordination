package org.example.common.network;

import org.example.expection.CoordinationException;

public class InvalidReceiveException extends CoordinationException {
    public InvalidReceiveException(String message) {
        super(message);
    }

    public InvalidReceiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
