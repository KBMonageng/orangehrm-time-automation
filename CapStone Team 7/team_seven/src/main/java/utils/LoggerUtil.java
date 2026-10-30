package utils;

import java.util.logging.Logger;

public class LoggerUtil {
    private LoggerUtil() {
    }

    public static Logger getLogger(Class<?> className) {
        return Logger.getLogger(className.getName());
    }
}
