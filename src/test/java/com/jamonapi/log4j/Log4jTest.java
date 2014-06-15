package com.jamonapi.log4j;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import com.jamonapi.MonitorFactory;

/** Tests that show that log4j properly calls the log4j {@code JAMonAppender} to aggregate calls and
 * generalize and aggregate individual messages.
 */
public class Log4jTest {

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        log();
    }


    @Test
    public void testLog4jCounts() {
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL","testlog4jUnits").getHits()).isEqualTo(1);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG","testlog4jUnits").getHits()).isEqualTo(2);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO","testlog4jUnits").getHits()).isEqualTo(3);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR","testlog4jUnits").getHits()).isEqualTo(4);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TOTAL","testlog4jUnits").getHits()).isEqualTo(10);
    }


    @Test
    public void testLog4jIndividualMessages() {
        assertThat(MonitorFactory.getMonitor("FATAL.com.jamonapi.log4j.fatal message ?","testlog4jUnits").getHits()).isEqualTo(1);
        assertThat(MonitorFactory.getMonitor("DEBUG.com.jamonapi.log4j.debug message ?","testlog4jUnits").getHits()).isEqualTo(2);
        assertThat(MonitorFactory.getMonitor("INFO.com.jamonapi.log4j.info message ?","testlog4jUnits").getHits()).isEqualTo(3);
        assertThat(MonitorFactory.getMonitor("ERROR.com.jamonapi.log4j.error message ?","testlog4jUnits").getHits()).isEqualTo(4);
    }


    private static Logger log() {
        PropertyConfigurator.configure(getDefaultProps());
        Logger logger = Logger.getLogger("com.jamonapi.log4j");
        logger.fatal("fatal message " + 1);
        logger.debug("debug message " + 2);
        logger.debug("debug message " + 2);
        logger.info("info message " + 3);
        logger.info("info message " + 3);
        logger.info("info message " + 3);
        logger.error("error message " + 4);
        logger.error("error message " + 4);
        logger.error("error message " + 4);
        logger.error("error message " + 4);
        return logger;
    }

    private static Properties getDefaultProps() {
        // # Set root logger level to DEBUG and its only appender to A1.
        Properties properties = new Properties();
        properties.put("log4j.logger.com.jamonapi.log4j", "DEBUG, A1, jamonAppender");

        // # A1 is set to be a ConsoleAppender, and A2 uses JAMonAppender.
        properties.put("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");

        properties.put("log4j.appender.jamonAppender", "com.jamonapi.log4j.JAMonAppender");

        properties.put("log4j.appender.jamonAppender.units", "testlog4jUnits");
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
