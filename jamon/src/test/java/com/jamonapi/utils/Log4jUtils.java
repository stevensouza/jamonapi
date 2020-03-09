package com.jamonapi.utils;


import com.jamonapi.MonitorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by stevesouza on 11/20/14.
 */
public class Log4jUtils {


    public static void log() {

    }
//    public static Logger log() {
//        PropertyConfigurator.configure(getDefaultProps());
//        Logger logger = org.apache.log4j.Logger.getLogger("com.jamonapi.log4j");
//
//        logger.trace("trace message " + 1);
//
//        logger.debug("debug message " + 2);
//        logger.debug("debug message " + 2);
//
//        logger.info("info message " + 3);
//        logger.info("info message " + 3);
//        logger.info("info message " + 3);
//
//        logger.warn("warn message " + 4);
//        logger.warn("warn message " + 4);
//        logger.warn("warn message " + 4);
//        logger.warn("warn message " + 4);
//
//        logger.error("error message " + 5);
//        logger.error("error message " + 5);
//        logger.error("error message " + 5);
//        logger.error("error message " + 5);
//        logger.error("error message " + 5);

//        logger.fatal("fatal message " + 6);
//        logger.fatal("fatal message " + 6);
//        logger.fatal("fatal message " + 6);
//        logger.fatal("fatal message " + 6);
//        logger.fatal("fatal message " + 6);
//        logger.fatal("fatal message " + 6);

//        return logger;
//    }

    private static Logger delme = LoggerFactory.getLogger(Log4jUtils.class);

    private static void delme() {
       delme.error("hi {}", 100);
       delme.info("bye");
       System.err.println(MonitorFactory.getReport());

    }

    public static void main(String[] args) {
        delme();
    }
/*
Loggerj ctx = (LoggerContext) LogManager.getContext(false);
Configuration config = ctx.getConfiguration();

PatternLayout layout = PatternLayout.newBuilder()
  .withConfiguration(config)
  .withPattern("%d{HH:mm:ss.SSS} %level %msg%n")
  .build();

Appender appender = FileAppender.newBuilder()
  .setConfiguration(config)
  .withName("programmaticFileAppender")
  .withLayout(layout)
  .withFileName("java.log")
  .build();

appender.start();
config.addAppender(appender);
 */

    private static Properties getDefaultProps() {
        // # Set root logger level to DEBUG and its only appender to A1.
        // Setting to the TRACE level enables logging of all levels greather than TRACE.  The
        //  order is TRACE,DEBUG,INFO,WARN,ERROR,FATAL
        Properties properties = new Properties();
        properties.put("log4j.logger.com.jamonapi.log4j", "TRACE, A1, jamonAppender");

        // # A1 is set to be a ConsoleAppender, and A2 uses JAMonAppender.
        properties.put("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");

        properties.put("log4j.appender.jamonAppender", "com.jamonapi.log4j.JAMonAppender");

        properties.put("log4j.appender.jamonAppender.units", "log4j");
        properties.put("log4j.appender.jamonAppender.enableDefaultGeneralizer", "true");

        properties.put("log4j.appender.jamonAppender.EnableListeners", "BASIC");
        properties.put("log4j.appender.jamonAppender.EnableListenerDetails", "true");

        properties.put("log4j.appender.jamonAppender.EnableLevelMonitoring", "true");
        properties.put("log4j.appender.jamonAppender.ListenerBufferSize", "200");

        // # jamonAppender uses PatternLayout.
        properties.put("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.A1.layout.ConversionPattern",
                "%-4r steve [%t] %-5p %c %x - %m%n");

        // # A1 uses PatternLayout.
        properties.put("log4j.appender.jamonAppender.layout", "org.apache.log4j.PatternLayout");
        properties.put("log4j.appender.jamonAppender.layout.ConversionPattern", "%p.%c.%m");

        return properties;
    }
}
