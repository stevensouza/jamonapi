package com.jamonapi.log4j;

import com.jamonapi.*;
import com.jamonapi.utils.Log4jUtils;
import com.jamonapi.utils.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests that show that log4j properly calls the log4j {@code JAMonAppender} to aggregate calls and
 * generalize and aggregate individual messages.
 *
 */
public class Log4jTest {

    public static final String LOG4J2_XML = "log4j2.xml";
    public static final String LOG4J2_DEFAULTS_XML = "log4j2_defaults.xml";

    @Before
    public void setUp() throws Exception {
        LogManager.shutdown();
        MonitorFactory.reset();
    }

    @Test
    public void testSl4j() {
        // note sl4j doesn't have FATAL
        Log4jUtils.logWithSl4j();

        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TOTAL","log4j").getHits()).isEqualTo(15);
    }

    @Test
    public void testLog4jCounts() {
        Log4jUtils.logWithLog4j(LOG4J2_XML);
        monitorHasExpectedHits();
    }


    @Test
    public void testLog4jIndividualMessages() {
        Log4jUtils.logWithLog4j(LOG4J2_XML);

        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TRACE: trace message ?","log4j").getHits()).isEqualTo(1);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG: debug message ?","log4j").getHits()).isEqualTo(2);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO: info message ?","log4j").getHits()).isEqualTo(3);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.WARN: warn message ?","log4j").getHits()).isEqualTo(4);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR: error message ?","log4j").getHits()).isEqualTo(5);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL: fatal message ?","log4j").getHits()).isEqualTo(6);
    }

    @Test
    public void testLog4jBuffers() {
        // note sl4j doesn't have FATAL
        Log4jUtils.logWithLog4j(LOG4J2_XML);

        assertThat(getRows(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TRACE","log4j"))).isEqualTo(1);
        assertThat(getRows(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG","log4j"))).isEqualTo(2);
        assertThat(getRows(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO","log4j"))).isEqualTo(3);
        assertThat(getRows(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.WARN","log4j"))).isEqualTo(4);
        assertThat(getRows(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR","log4j"))).isEqualTo(5);
        assertThat(getRows(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL","log4j"))).isEqualTo(6);
        assertThat(getRows(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TOTAL","log4j"))).isEqualTo(21);
    }

    @Test
    public void testLog4jConfig() {
        Log4jUtils.logWithLog4j(LOG4J2_XML);

        JAMonAppender appender = Log4jUtils.getJAMonAppender();
        assertThat(appender.getUnits()).isEqualTo("log4j");
        assertThat(appender.getListenerBufferSize()).isEqualTo(400);
        assertTrue(appender.getEnableLevelMonitoring());
        assertTrue(appender.getGeneralize());
        monitorHasAllListeners();
    }

    @Test
    public void testLog4jConfig_withDefaults() {
        Log4jUtils.logWithLog4j(LOG4J2_DEFAULTS_XML);

        JAMonAppender appender = Log4jUtils.getJAMonAppender();
        assertThat(appender.getUnits()).isEqualTo("log4j");
        assertThat(appender.getListenerBufferSize()).isEqualTo(100);
        assertTrue(appender.getEnableLevelMonitoring());
        assertFalse(appender.getGeneralize());
        monitorHasNoListeners();
    }

    @Test
    public void testLog4jCounts_withDefaults() {
        Log4jUtils.logWithLog4j(LOG4J2_DEFAULTS_XML);
        monitorHasExpectedHits();
    }


    @Test
    public void testLog4jIndividualMessages_withDefaults() {
        Log4jUtils.logWithLog4j(LOG4J2_DEFAULTS_XML);

        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TRACE: trace message ?","log4j").getHits()).isEqualTo(0);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG: debug message ?","log4j").getHits()).isEqualTo(0);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO: info message ?","log4j").getHits()).isEqualTo(0);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.WARN: warn message ?","log4j").getHits()).isEqualTo(0);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR: error message ?","log4j").getHits()).isEqualTo(0);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL: fatal message ?","log4j").getHits()).isEqualTo(0);
    }

    @Test
    public void testLog4jBuffers_withDefaults() {
        Log4jUtils.logWithLog4j(LOG4J2_DEFAULTS_XML);
        monitorHasNoListeners();
    }

    @Test
    public void testSerialization() throws Throwable {
        Log4jUtils.logWithLog4j(LOG4J2_XML);

        // serialize and deserialize monitors
        MonitorComposite original = MonitorFactory.getRootMonitor();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        SerializationUtils.serialize(original, outputStream);
        MonitorComposite deserialized = SerializationUtils.deserialize(new ByteArrayInputStream(outputStream.toByteArray()));
        assertThat(original.getNumRows()).isEqualTo(deserialized.getNumRows());
    }

    private void monitorHasExpectedHits() {
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TRACE", "log4j").getHits()).isEqualTo(1);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG", "log4j").getHits()).isEqualTo(2);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO", "log4j").getHits()).isEqualTo(3);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.WARN", "log4j").getHits()).isEqualTo(4);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR", "log4j").getHits()).isEqualTo(5);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL", "log4j").getHits()).isEqualTo(6);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TOTAL", "log4j").getHits()).isEqualTo(21);
    }

    private void monitorHasNoListeners() {
        MonitorFactoryInterface f = MonitorFactory.getFactory();
        assertFalse(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TRACE","log4j").hasListeners());
        assertFalse(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG","log4j").hasListeners());
        assertFalse(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO","log4j").hasListeners());
        assertFalse(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.WARN","log4j").hasListeners());
        assertFalse(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR","log4j").hasListeners());
        assertFalse(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL","log4j").hasListeners());
        assertFalse(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TOTAL","log4j").hasListeners());
    }

    private void monitorHasAllListeners() {
        MonitorFactoryInterface f = MonitorFactory.getFactory();
        assertTrue(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TRACE","log4j").hasListeners());
        assertTrue(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG","log4j").hasListeners());
        assertTrue(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO","log4j").hasListeners());
        assertTrue(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.WARN","log4j").hasListeners());
        assertTrue(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR","log4j").hasListeners());
        assertTrue(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL","log4j").hasListeners());
        assertTrue(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TOTAL","log4j").hasListeners());
    }

    private int getRows(Monitor monitor) {
        JAMonBufferListener log4jBufferListener = (JAMonBufferListener) monitor.getListenerType("value")
                .getListener("FIFOBuffer");
        return log4jBufferListener==null ? 0 : log4jBufferListener.getRowCount();
    }

}
