package webtools.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: webtools-logging
 *
 * Created: 10. 12. 2015
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class GenericLogger {

    public static void info(Class<?> clazz, String message) {
        final Logger logger = LoggerFactory.getLogger(clazz);
        logger.info(message);
    }

    public static void error(Class<?> clazz, String message) {
        final Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(message);
    }

    public static void error(Class<?> clazz, Throwable e) {
        final Logger logger = LoggerFactory.getLogger(clazz);
        logger.error("ERROR", e);
    }

    public static void error(Class<?> clazz, Throwable e, String message) {
        final Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(message, e);
    }
}
