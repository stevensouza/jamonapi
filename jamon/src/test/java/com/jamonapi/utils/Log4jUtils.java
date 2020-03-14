package com.jamonapi.utils;


import com.jamonapi.log4j.JAMonAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by stevesouza on 11/20/14.
 * <p>
 * Uses both sl4j and log4j. sl4j calls the underlying log4j, but it just shows the jamon appender works in both cases.
 */
public class Log4jUtils {


    public static Logger logWithSl4j() {
        // automatically loads log4j2.xml
        // note sl4j does not support fatal
        Configurator.initialize(null, "log4j2.xml");
        Logger logger = LoggerFactory.getLogger(Log4jUtils.class);

        logger.trace("trace message " + 1);

        logger.debug("debug message " + 2);
        logger.debug("debug message " + 2);

        logger.info("info message " + 3);
        logger.info("info message " + 3);
        logger.info("info message " + 3);

        logger.warn("warn message " + 4);
        logger.warn("warn message " + 4);
        logger.warn("warn message " + 4);
        logger.warn("warn message " + 4);

        logger.error("error message " + 5);
        logger.error("error message " + 5);
        logger.error("error message " + 5);
        logger.error("error message " + 5);
        logger.error("error message " + 5);

        return logger;
    }

    public static org.apache.logging.log4j.Logger logWithLog4j(String xmlConfigFile) {
        Configurator.initialize(null, xmlConfigFile);
        org.apache.logging.log4j.Logger logger = LogManager.getLogger();

        logger.trace("trace message " + 1);

        logger.debug("debug message " + 2);
        logger.debug("debug message " + 2);

        logger.info("info message " + 3);
        logger.info("info message " + 3);
        logger.info("info message " + 3);

        logger.warn("warn message " + 4);
        logger.warn("warn message " + 4);
        logger.warn("warn message " + 4);
        logger.warn("warn message " + 4);

        logger.error("error message " + 5);
        logger.error("error message " + 5);
        logger.error("error message " + 5);
        logger.error("error message " + 5);
        logger.error("error message " + 5);

        logger.fatal("fatal message " + 6);
        logger.fatal("fatal message " + 6);
        logger.fatal("fatal message " + 6);
        logger.fatal("fatal message " + 6);
        logger.fatal("fatal message " + 6);
        logger.fatal("fatal message " + 6);

        return logger;
    }

    public static JAMonAppender getJAMonAppender() {
        LoggerContext lc = (LoggerContext) LogManager.getContext(false);
        System.err.println("****" + lc.getConfiguration().getAppender("JamonAppender"));
        return lc.getConfiguration().getAppender("JamonAppender");
    }

    public static void shutdownLog4j() {
        LogManager.shutdown();
    }


}
