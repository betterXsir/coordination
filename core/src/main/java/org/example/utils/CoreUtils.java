package org.example.utils;

import org.slf4j.Logger;
import org.slf4j.event.Level;

public class CoreUtils {
    public static void swallow(RunnableWrapper runnable, Logger logger, Level logLevel) {
        try {
            runnable.run();
        } catch (Throwable t) {
            switch (logLevel) {
                case ERROR: logger.error(t.getMessage(), t);
                case WARN: logger.warn(t.getMessage(), t);
                case INFO: logger.info(t.getMessage(), t);
                case DEBUG: logger.debug(t.getMessage(), t);
                case TRACE: logger.trace(t.getMessage(), t);
            }
        }
    }
}
