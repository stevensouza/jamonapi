package com.jamonapi.log4j;

import com.jamonapi.MonitorFactory;
import com.jamonapi.utils.Log4jUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests that show that log4j properly calls the log4j {@code JAMonAppender} to aggregate calls and
 * generalize and aggregate individual messages.
 */
public class Log4jTest {

    @Before
    public void setUp() throws Exception {
        MonitorFactory.reset();
        Log4jUtils.log();
    }


    @Test
    public void testLog4jCounts() {
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TRACE","log4j").getHits()).isEqualTo(1);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.DEBUG","log4j").getHits()).isEqualTo(2);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.INFO","log4j").getHits()).isEqualTo(3);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.WARN","log4j").getHits()).isEqualTo(4);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.ERROR","log4j").getHits()).isEqualTo(5);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.FATAL","log4j").getHits()).isEqualTo(6);
        assertThat(MonitorFactory.getMonitor("com.jamonapi.log4j.JAMonAppender.TOTAL","log4j").getHits()).isEqualTo(21);
    }


    @Test
    public void testLog4jIndividualMessages() {
        assertThat(MonitorFactory.getMonitor("TRACE.com.jamonapi.log4j.trace message ?","log4j").getHits()).isEqualTo(1);
        assertThat(MonitorFactory.getMonitor("DEBUG.com.jamonapi.log4j.debug message ?","log4j").getHits()).isEqualTo(2);
        assertThat(MonitorFactory.getMonitor("INFO.com.jamonapi.log4j.info message ?","log4j").getHits()).isEqualTo(3);
        assertThat(MonitorFactory.getMonitor("WARN.com.jamonapi.log4j.warn message ?","log4j").getHits()).isEqualTo(4);
        assertThat(MonitorFactory.getMonitor("ERROR.com.jamonapi.log4j.error message ?","log4j").getHits()).isEqualTo(5);
        assertThat(MonitorFactory.getMonitor("FATAL.com.jamonapi.log4j.fatal message ?","log4j").getHits()).isEqualTo(6);
    }




}
